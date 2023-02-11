package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    public Section() {

    }

    public Section(final Line line, final Station upStation, final Station downStation, final int distance) {
        this(line, upStation, downStation, new Distance(distance));
    }

    public Section(final Line line, final Station upStation, final Station downStation, final Distance distance) {
        validateSection(upStation, downStation, distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateSection(final Station upStation, final Station downStation, final Distance distance) {
        validateStation(upStation);
        validateStation(downStation);
        validateDistance(distance);
    }

    private void validateStation(final Station station) {
        if (station == null) {
            throw new IllegalArgumentException("구간의 역은 null이 될 수 없습니다.");
        }
    }

    private void validateDistance(final Distance distance) {
        if (distance.isUnderMin()) {
            throw new IllegalArgumentException("구간의 생성 최소 값 보다 작습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
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

    public boolean isContain(final Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    public boolean isUpStation(final Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStation(final Station station) {
        return this.downStation.equals(station);
    }

    public Section setDownStation(final Station downStation) {
        this.downStation = downStation;
        return this;
    }

    public Section setUpStation(final Station upStation) {
        this.upStation = upStation;
        return this;
    }

    public void minusDistance(final Distance distance) {
        this.distance.minus(distance);
    }
}
