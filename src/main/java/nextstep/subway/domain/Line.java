package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

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
        this.sections.addSection(upStation, downStation, distance, this);
    }

    public void removeSection(final Long stationId) {
        this.sections.removeSection(stationId);
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }
}
