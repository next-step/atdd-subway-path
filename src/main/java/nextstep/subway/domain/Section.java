package nextstep.subway.domain;

import javax.persistence.*;

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

    private int distance;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isExistAnyStation(Section other) {
        return isEqualsUpStation(other) || isEqualsDownStation(other)
                || isNext(other) || isPrevious(other);
    }

    public boolean isEqualsAllStations(Section other) {
        return isEqualsUpStation(other) && isEqualsDownStation(other);
    }

    public boolean isNext(Section other) {
        return upStation.equals(other.downStation);
    }

    public boolean isPrevious(Section other) {
        return downStation.equals(other.upStation);
    }

    public void updateToMiddleStation(Section newSection) {
        distance -= newSection.distance;
        if (isEqualsUpStation(newSection)) {
            upStation = newSection.downStation;
            return;
        }

        downStation = newSection.upStation;
    }

    public boolean isEqualsUpStation(Section other) {
        return upStation.equals(other.upStation);
    }

    public boolean isEqualsDownStation(Section other) {
        return downStation.equals(other.downStation);
    }

    public boolean isGreaterThanOrEqualDistance(Section other) {
        return distance >= other.distance;
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

    public int getDistance() {
        return distance;
    }
}
