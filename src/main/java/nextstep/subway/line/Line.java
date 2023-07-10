package nextstep.subway.line;

import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    public Long getDistance() {
        return distance;
    }

    @Column(nullable = false)
    private Long distance = 0L;

    @Embedded
    private LineStations lineStations;

    protected Line() {
    }

    public Line(final String name, final String color, final Long distance, final List<Section> lineStations) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.lineStations = new LineStations(lineStations);
    }

    public Line(final String name, final String color, final Long distance) {
        this(name, color, distance, new ArrayList<>());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return this.color;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public boolean isLastStation(Station station) {
        return lineStations.isLastStation(station);
    }

    public Section addSection(Station downStation, Long distance) {
        return lineStations.addSection(new Section(this, downStation, distance));
    }

    public List<Station> getStations() {
        return lineStations.getStations();
    }

    public Section removeSection(Station station) {
        return this.lineStations.removeSection(station);
    }

    public long countOfStations() {
        return lineStations.countOfStations();
    }
}
