package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.common.exception.ApplicationType;
import nextstep.subway.station.domain.Station;

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
    private Sections sections;


    public Line() {
        sections = new Sections();
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.sections = new Sections();

        this.name = name;
        this.color = color;
        this.sections.addSection(this, upStation, downStation, distance);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<Section> getSections() {
        return this.sections.getSections();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    public List<Station> getSortedStations() {
        return this.sections.getSortedStations();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.sections.addSection(this, upStation, downStation, distance);
    }

    public void removeSection(Station station) {
        this.sections.removeSection(station);
    }

    public int getLineDistance() {
        return this.sections.getTotalDistance();
    }
}
