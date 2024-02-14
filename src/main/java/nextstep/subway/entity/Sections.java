package nextstep.subway.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {

    public static final int MIN_DELETE_REQUIRED_SECTIONS_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSection(Section sectionToAdd) {
        if (isAddFirstSection()) {
            sections.add(sectionToAdd);
            return;
        }
        if (isAddLastSection(sectionToAdd)) {
            sections.add(sectionToAdd);
            return;
        }
        insertSection(sectionToAdd);
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
                .anyMatch(section -> section.isAtLeastOneSameStation(station));
    }

    public boolean hasNoSections() {
        return sections.isEmpty();
    }

    private boolean isAddFirstSection() {
        return sections.isEmpty();
    }

    private void insertSection(Section sectionToAdd) {
        // 1. 삽입할 구간 확인
        Section insertionSection = findInsertionSection(sectionToAdd);

        // 2. 삽입할 역 확인
        Station insertionStation = insertionSection.findCommonStation(sectionToAdd);

        if (isFirstStation(insertionStation)) {
            if(insertionStation.isSame(sectionToAdd.getDownStation())) {
                int insertionIndex = sections.indexOf(insertionSection);

                sections.add(insertionIndex, sectionToAdd);
                return;
            }
            if(insertionStation.isSame(sectionToAdd.getUpStation())) {
                int insertionIndex = sections.indexOf(insertionSection);
                Section nextSection = sections.get(insertionIndex);

                sections.set(insertionIndex, sectionToAdd);
                sections.add(insertionIndex + 1, createNextSection(sectionToAdd, nextSection));
                return;
            }
        }

        // 3. 삽입할 인덱스 확인
        if(insertionStation.isSame(sectionToAdd.getUpStation())) {
            int insertionIndex = sections.indexOf(insertionSection);
            Section nextSection = sections.get(insertionIndex + 1);

            sections.set(insertionIndex + 1, sectionToAdd);
            sections.add(insertionIndex + 2, createNextSection(sectionToAdd, nextSection));
            return;
        }

        if(insertionStation.isSame(sectionToAdd.getDownStation())) {
            int insertionIndex = sections.indexOf(insertionSection);

            sections.add(insertionIndex + 1, sectionToAdd);
            sections.set(insertionIndex, createBeforeSection(sectionToAdd, insertionSection));
        }
    }

    private boolean isFirstStation(Station insertionStation) {
        return sections.get(0).getUpStation().isSame(insertionStation);
    }

    private Section createBeforeSection(Section sectionToAdd, Section sectionOfIndex) {
        return new Section(
                sectionOfIndex.getUpStation(),
                sectionToAdd.getUpStation(),
                sectionOfIndex.getDistance() - sectionToAdd.getDistance());
    }


    private Section createNextSection(Section sectionToAdd, Section sectionOfIndex) {
        return new Section(
                sectionToAdd.getDownStation(),
                sectionOfIndex.getDownStation(),
                sectionOfIndex.getDistance() - sectionToAdd.getDistance());
    }

    private Section findInsertionSection(Section sectionToAdd) {
        return sections.stream()
                .filter(section ->
                                section.isAtLeastOneSameStation(sectionToAdd.getUpStation()) ||
                                section.isAtLeastOneSameStation(sectionToAdd.getDownStation()))
                .findFirst()
                .orElseThrow(RuntimeException::new);


    }

    private boolean isAddLastSection(Section section) {
        return findLastStation().isSame(section.getUpStation());
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Section> getAllSections() {
        return sections;
    }
}
