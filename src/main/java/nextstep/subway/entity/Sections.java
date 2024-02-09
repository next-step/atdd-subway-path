package nextstep.subway.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

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

    public boolean areAllUpStationsDifferentFrom(Section newSection) {
        return sections.stream()
                .noneMatch(existSection -> existSection.isUpStationSame(newSection.getDownStation()));
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

    public List<Section> getSections() {
        return sections;
    }

    public boolean isConnectToLastStation(Section toSaveSection) {
        return findLastStation().equals(toSaveSection.getUpStation());
    }
}
