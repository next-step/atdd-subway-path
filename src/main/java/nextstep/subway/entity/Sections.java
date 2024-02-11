package nextstep.subway.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {

    public static final int MIN_DELETE_REQUIRED_SECTIONS_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
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

    public void deleteSection(Station stationToDelete) {
        Section sectionToDelete = sections.stream()
                .filter(section -> section.getDownStation().equals(stationToDelete))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        sections.remove(sectionToDelete);
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
