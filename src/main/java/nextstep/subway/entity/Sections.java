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
        if(isAddFirstSection(section)) {
            sections.add(section);
            return;
        }
        if(isAddIntermediateSection(section)) {
            addIntermediateSection(section);
            return;
        }
        if(isAddLastSection(section)) {
            sections.add(section);
        }
    }

    private boolean isAddFirstSection(Section section) {
        return sections.isEmpty();
    }

    private void addIntermediateSection(Section section) {
        // 삽입 지점 찾기
        int indexToIntermediate = IntStream.range(0, sections.size())
                .filter(i -> sections.get(i).getUpStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        
        // 직후 역 만들기 ...
        Section existNextSection = sections.get(indexToIntermediate);
        Station nextStation = existNextSection.getDownStation();

        // 직후 구간 만들기 ...
        Section nextSection = new Section(section.getDownStation(), nextStation,
                existNextSection.getDistance() - section.getDistance());

        // 삽입
        sections.set(indexToIntermediate, section);
        sections.add(indexToIntermediate + 1, nextSection);
    }

    private boolean isAddIntermediateSection(Section section) {
        return sections.stream()
                .anyMatch(exists -> exists.getUpStation().equals(section.getUpStation()));
    }

    private boolean isAddLastSection(Section section) {
        return findLastStation().equals(section.getUpStation());
    }

    public Station findFirstUpStation() {
        return sections.get(0).getUpStation();
    }

    public boolean hasExistingStationExceptLast(Section newSection) {
        return sections.stream()
                .limit(sections.size() - 1)
                .anyMatch(section ->section.isLeastOneSameStation(newSection));
    }

    public boolean isDeletionAllowed() {
        return sections.size() > MIN_DELETE_REQUIRED_SECTIONS_SIZE;
    }

    public boolean canSectionDelete(Station stationToDelete) {
        if(sections.isEmpty()) {
           throw new IllegalArgumentException("해당 노선에 구간이 존재하지 않습니다.");
        }
        if(!isDeletionAllowed()) {
            throw new IllegalArgumentException("해당 노선에 구간이 최소 2개 이상일 경우 삭제가 가능합니다.");
        }
        if(!findLastStation().isSame(stationToDelete)) {
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

    public boolean isConnectToLastStation(Section toSaveSection) {
        return findLastStation().equals(toSaveSection.getUpStation());
    }

    public boolean hasNoSections() {
        return sections.isEmpty();
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Section> getAllSections() {
        return sections;
    }
}
