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

    public void addSection(Section section) {
        sections.add(section);
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
}
