package nextstep.subway.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Embeddable
public class Sections {

    public static final int MIN_DELETE_REQUIRED_SECTIONS_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section section) {
        if (isAddFirstSection(section)) {
            sections.add(section);
            return;
        }
        if (isInsertSection(section)) {
            insertSection(section);
            return;
        }
        if (isAddLastSection(section)) {
            sections.add(section);
        }
    }

    public boolean isDeletionAllowed() {
        return sections.size() > MIN_DELETE_REQUIRED_SECTIONS_SIZE;
    }

    public boolean canSectionDelete(Station stationToDelete) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("해당 노선에 구간이 존재하지 않습니다.");
        }
        if (!isDeletionAllowed()) {
            throw new IllegalArgumentException("해당 노선에 구간이 최소 2개 이상일 경우 삭제가 가능합니다.");
        }
        if (!findLastStation().isSame(stationToDelete)) {
            throw new IllegalArgumentException("마지막 구간의 하행역과 동일하지 않습니다.");
        }
        return true;
    }

    public void deleteLastSection() {
        sections.remove(sections.size() - 1);
    }

    public Station findLastStation() {
        if (sections.isEmpty()) {
            throw new RuntimeException();
        }
        return sections.get(sections.size() - 1).getDownStation();
    }

    public Station findFirstStation() {
        return sections.get(0).getUpStation();
    }

    public List<Station> getAllStations() {
        Set<Station> allStations = new HashSet<>();
        sections.forEach(section -> {
            allStations.add(section.getUpStation());
            allStations.add(section.getDownStation());
        });
        return new ArrayList<>(allStations);
    }

    public boolean hasExistingStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isLeastOneSameStation(station));
    }

    public boolean hasNoSections() {
        return sections.isEmpty();
    }

    private boolean isAddFirstSection(Section section) {
        return sections.isEmpty();
    }

    private void insertSection(Section sectionToAdd) {
        int insertionIndex = findInsertionIndex(sectionToAdd);
        Section currentSection = sections.get(insertionIndex);

        sections.set(insertionIndex, sectionToAdd);
        sections.add(insertionIndex + 1, createNextSection(sectionToAdd, currentSection));
    }

    private Section createNextSection(Section sectionToAdd, Section sectionOfIndex) {
        return new Section(
                sectionToAdd.getDownStation(),
                sectionOfIndex.getDownStation(),
                sectionOfIndex.getDistance() - sectionToAdd.getDistance());
    }

    private int findInsertionIndex(Section sectionToAdd) {
        return IntStream.range(0, sections.size())
                .filter(i -> sections.get(i).isUpStationSame(sectionToAdd))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private boolean isInsertSection(Section section) {
        return sections.stream()
                .anyMatch(exists -> exists.getUpStation().equals(section.getUpStation()));
    }

    private boolean isAddLastSection(Section section) {
        return findLastStation().equals(section.getUpStation());
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Section> getAllSections() {
        return sections;
    }
}
