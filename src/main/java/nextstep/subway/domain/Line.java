package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

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

    public String getColor() {
        return color;
    }

    public void modifyNameAndColor(String name, String color) {
        this.name = Optional.ofNullable(name).orElse(this.name);
        this.color = Optional.ofNullable(color).orElse(this.color);
    }

    public List<Section> getSections() {
        return this.sections.getSections();
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }

    public void deleteSection(Station station) {
        this.sections.deleteSection(station);
    }
}
