package nextstep.subway.line;

import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private Long distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Long distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Long id, Line line, Station upStation, Station downStation, Long distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean hasStation(Station station) {
        return upStation.isSameStation(station) || downStation.isSameStation(station);
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

    public Long getDistance() {
        return distance;
    }

    public boolean isMatchWithUpStation(Station station) {
        return upStation.isSameStation(station);
    }

    public boolean isMatchWithDownStation(Station station) {
        return downStation.isSameStation(station);
    }

    public void updateUpStationAndDecreaseDistance(Station station, Long distance) {
        validateHasLongerDistanceThan(distance);
        this.upStation = station;
        this.distance -= distance;
    }

    public void updateDownStationAndDecreaseDistance(Station station, Long distance) {
        validateHasLongerDistanceThan(distance);
        this.downStation = station;
        this.distance -= distance;
    }

    public void updateDownStationAndIncreaseDistance(Section other) {
        this.downStation = other.downStation;
        this.distance += other.distance;
    }

    private void validateHasLongerDistanceThan(Long otherDistance) {
        if (distance <= otherDistance) {
            throw new IllegalArgumentException("기존 구간보다 길거나 같은 구간을 추가할 수 없습니다. 구간 길이: " + otherDistance);
        }
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
