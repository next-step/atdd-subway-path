package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean has(Station station) {
        return isUpStationEquals(station) || isDownStationEquals(station);
    }

    public boolean isUpStationEquals(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isDownStationEquals(Station downStation) {
        return this.downStation.equals(downStation);
    }

    public int getDistance() {
        return distance;
    }

    public void updateUpStation(Station upStation, int distance) {
        validateUpdateDistance(distance);
        this.upStation = upStation;
        this.distance -= distance;
    }

    public void updateDownStation(Station downStation, int distance) {
        validateUpdateDistance(distance);
        this.downStation = downStation;
        this.distance -= distance;
    }

    private void validateUpdateDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("기존 구간의 길이보다 긴 구간은 추가할 수 없습니다.");
        }
    }

    public boolean isExistUpStation() {
        return this.upStation != null;
    }

    public boolean isExistDownStation() {
        return this.downStation != null;
    }
}
