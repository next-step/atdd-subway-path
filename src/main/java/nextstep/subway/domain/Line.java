package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Station> getAllStations() {
        // list of stations that first is upStation and last is downStation
        Section firstSection = getFirstSection();
        Section lastSection = getLastSection();

        // order all stations by firstSection and lastSection
        LinkedList<Station> stations = new LinkedList<>();
        while (true) {
            stations.add(firstSection.getUpStation());
            stations.add(firstSection.getDownStation());
            if (firstSection.equals(lastSection)) {
                break;
            }
            firstSection = getSectionByUpStation(firstSection.getDownStation());
        }
        return stations.stream().distinct().collect(Collectors.toList());
    }

    public void addSection(Section section) {
        sections.addSection(this, section);
    }

    private Section getSectionByUpStation(Station upStation) {
        return sections.getSectionByUpStation(upStation);
    }

    private Section getFirstSection() {
        // find upStation that is not downStation of other section.
        return sections.getFirstSection();
    }

    private Section getLastSection() {
        // find downStation that is not upStation of other section.
        return sections.getLastSection();
    }

    public void removeSection(Station deleteStation) {
        sections.removeSection(deleteStation);
    }

    public boolean isSectionsEmpty() {
        return sections.isEmpty();
    }
}
