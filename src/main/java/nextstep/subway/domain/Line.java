package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
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

    public Sections getSections() {
        return sections;
    }

    protected void addSections(Section section) {
        sections.addSection(section);
    }

    public boolean isEmptySections() {
        return sections.isEmpty();
    }

    public List<Station> getAllStations() {
        return sections.getStations();
    }

    public boolean equalsLastStation(Station station) {
        return sections.equalsLastStation(station);
    }

    public void removeLastSection() {
        sections.removeLast();
    }
}
