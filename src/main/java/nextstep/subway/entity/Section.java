package nextstep.subway.entity;

import javax.persistence.*;

@Entity
public class Section {

    public static final int MIN_STATION_ID_VALUE = 1;

    public static final int MIN_DISTANCE_VALUE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private int distance;

    @ManyToOne
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = validateDistance(distance);
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = validateDistance(distance);
        this.line = line;
    }

    public boolean isConnectToLastUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean areStationsSame() {
        return upStation.equals(downStation);
    }

    public boolean isUpStationSame(Station station) {
        return this.upStation.equals(station);
    }

    public Section setLine(Line line) {
        this.line = line;
        line.getSections().addSection(this);

        return this;
    }

    private int validateDistance(Integer distance) {
        if(distance == null || distance < MIN_DISTANCE_VALUE) {
            throw new IllegalArgumentException("거리는 0보다 커야합니다.");
        }
        return distance;
    }

    public Long getId() {
        return id;
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
