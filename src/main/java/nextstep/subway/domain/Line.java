package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public void removeSection(Station station) {
        sections.removeLastSection(station);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
