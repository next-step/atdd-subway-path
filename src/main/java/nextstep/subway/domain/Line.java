package nextstep.subway.domain;

import javax.persistence.*;

import java.util.List;

import lombok.Getter;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Section newSection) {
        sections.add(newSection);
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public List<Station> getSortedStations() {
        return sections.getSortedStations();
    }
}
