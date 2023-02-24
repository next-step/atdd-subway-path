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
    private Sections sections;

    public Line() {
    }

    private Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color) {
        this(null, name, color, new Sections());
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this(null, name, color, new Sections(List.of(new Section(upStation, downStation, distance))));
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.addSection(new Section(this, upStation, downStation, distance));
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

    public List<Station> getStations() {
        return sections.getStations();
    }

    public int getDistance() {
        return sections.getDistance();
    }

    public void setLineName(String name) {
        this.name = name;
    }

    public void setLineColor(String color) {
        this.color = color;
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
    }
}
