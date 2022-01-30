package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    public Line() {
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections.add(section);
    }

    public Line(Long lineId, String name, String color, Section section) {
        this.id = lineId;
        this.name = name;
        this.color = color;
        this.sections.add(section);
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public static Line of(String name, String color, Section section) {
        return new Line(name, color, section);
    }

    public static Line of(Long lineId, String name, String color, Section section) {
        return new Line(lineId, name, color, section);
    }

    public Long getId() {
        return id;
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

    public void addSection(Section section) {
        sections.add(section);
        section.setLine(this);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public boolean alreadyHas(Section section) {
        return sections.hasAllStationsOf(section);
    }
}
