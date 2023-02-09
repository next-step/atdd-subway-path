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

    private Section(final Long id, final Line line, final Station upStation, final Station downStation, final Distance distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(final Station upStation, final Station downStation, final Integer distance) {
        this(null, null, upStation, downStation, new Distance(distance));
    }

    public Section(final Line line, final Station upStation, final Station downStation, final Distance distance) {
        this(null, line, upStation, downStation, new Distance(distance.getDistance()));
    }

    Section(final Line line, final Station upStation, final Station downStation, final Integer distance) {
        this(null, line, upStation, downStation, new Distance(distance));
    }

    public void changeSectionOfUpStation(final Station upStation, final Integer distance) {
        this.downStation = this.upStation;
        this.upStation = upStation;
        this.distance.change(distance);
    }

    public void changeSectionOfMiddleStation(final Station downStation, final Integer distance) {
        this.downStation = downStation;
        this.distance.minus(distance);
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

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public void mergeSection(final Section section) {
        this.downStation = section.downStation;
        this.distance.plus(section.distance.getDistance());
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