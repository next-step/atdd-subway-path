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

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, PairedStations pairedStations, int distance) {
        this(name, color);
        addSection(pairedStations, distance);
    }

    public Section addSection(PairedStations pairedStations, int distance) {
        Section section = new Section(this, pairedStations.getUpStation(), pairedStations.getDownStation(), distance);
        return sections.add(section);
    }

    public void deleteSection(Station station) {
        sections.delete(station);
    }

    public List<Station> getStations() {
        return sections.flatStations();
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

    public Line update(Line updateLine) {
        this.name = updateLine.getName();
        this.color = updateLine.getColor();

        return this;
    }
}
