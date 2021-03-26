package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.List;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(Long userId, String name, String color) {
        if (name.length() < 2 || name.length() > 10) {
            throw new InvalidLineException();
        }
        this.userId = userId;
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void addSection(Station upStation, Station downStation, int distance, int duration) {
        sections.addSection(new Section(this, upStation, downStation, distance, duration));
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
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
}
