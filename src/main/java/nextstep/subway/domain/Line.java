package nextstep.subway.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Line {
    @Embedded
    private final Sections sections = new Sections();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 20, nullable = false)
    private String color;

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;

        Section section = new Section(upStation, downStation, distance, this);
        this.sections.add(section);
    }

    public Line(Long id ,String name, String color, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;

        Section section = new Section(upStation, downStation, distance, this);
        this.sections.add(section);
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

    public int totalDistance() {
        return this.sections.totalDistance();
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(final String color) {
        this.color = color;
    }

    public void addSection(final Station upStation, final Station downStation, final int distance) {
        if (distance < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "거리는 1 이하 일 수 없습니다.");
        }

        this.sections.addSection(upStation, downStation, distance, this);
    }

    public void removeSection(final Station station) {
        this.sections.removeSection(station);
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }

    public List<Section> getSections() {
        return this.sections.getSections();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Line line = (Line) o;
        return Objects.equals(sections, line.sections) && Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections, id, name, color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "sections=" + sections +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
