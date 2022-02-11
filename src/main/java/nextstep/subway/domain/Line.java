package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;

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

    public Line(String name, String color, Station startingStation, Station endingStation, int distance) {
        this.name = name;
        this.color = color;
        sections.add(new Section(this, startingStation, endingStation, distance));
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    Line(Long id, String name, String color, Station startingStation, Station endingStation, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        sections.add(new Section(this, startingStation, endingStation, distance));
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

    public void update(String name, String color) {
        if (!Objects.isNull(name)) {
            this.name = name;
        }
        if (!Objects.isNull(color)) {
            this.color = color;
        }
    }

    public void addSection(Station startingStation, Station endingStation, int distance) {
        sections.add(new Section(this, startingStation, endingStation, distance));
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }

    public void registerInLineMap(PathFinder pathFinder) {
        sections.registerInLineMap(pathFinder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
