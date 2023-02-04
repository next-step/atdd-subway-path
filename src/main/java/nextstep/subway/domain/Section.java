package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {}

    Section(final Long id, final Line line, final Station upStation, final Station downStation, final Integer distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public Section(final Long id, final Station upStation, final Station downStation, final Integer distance) {
        this(id, null, upStation, downStation, distance);
    }

    Section(final Line line, final Station upStation, final Station downStation, final Integer distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(final Station upStation, final Station downStation, final Integer distance) {
        this(null, null, upStation, downStation, distance);
    }

    void addLine(final Line line) {
        this.line = line;
    }

    boolean matchUpStation(final Station station) {
        return this.upStation.equals(station);
    }

    boolean matchDownStation(final Station station) {
        return this.downStation.equals(station);
    }

    void validateDistanceGreaterThan(final Integer distance) {
        this.distance.validateGreaterThan(distance);
    }

    void minus(final Integer distance) {
        this.distance.minus(distance);
    }

    void changeDownStation(final Station station) {
        this.downStation = station;
    }

    void changeStation(final Station upStation) {
        this.downStation = this.upStation;
        this.upStation = upStation;
    }

    void changeDistance(final Integer distance) {
        this.distance.change(distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}