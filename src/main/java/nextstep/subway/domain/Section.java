package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.domain.exception.SectionStationCanNotBeNullException;

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
        validateSection(upStation, downStation);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateSection(final Station upStation, final Station downStation) {
        validateStation(upStation);
        validateStation(downStation);
    }

    private void validateStation(final Station station) {
        if (station == null) {
            throw new SectionStationCanNotBeNullException();
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
