package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {}

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

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void updateSections(Section section) {
        this.sections = new Sections();
        this.sections.add(section);
    }

    public void addSections(Section section) {
        this.sections.add(section);
    }

    public void removeSection(Station station) {
        this.sections.remove(station);
    }
}
