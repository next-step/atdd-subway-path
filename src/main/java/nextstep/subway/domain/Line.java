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

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> findAllStations() {
        if (sections.isEmpty()) {
            return List.of();
        }
        return sections.allStations();
    }

    public void removeSection(Station station) {
        sections.delete(station);
    }

    public List<Section> getSections() {
        return sections.getSections();
    }
}
