package nextstep.subway.section.domain;

import javax.persistence.*;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Integer distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        validateStation(upStation, downStation);
        validateDistance(distance);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("upStation과 downStation은 같을 수 없습니다.");
        }
    }

    private void validateDistance(Integer distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 길이는 0보다 커야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Integer getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public String getUpStationName() {
        return upStation.getName();
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public String getDownStationName() {
        return downStation.getName();
    }

    public boolean upStationEqualsTo(Station downStation) {
        return downStation.equals(upStation) || downStation.equals(this.downStation);
    }

    public void assignLine(Line line) {
        this.line = line;
    }

    public boolean downStationEqualsTo(Station station) {
        return downStation.equals(station);
    }

    public boolean hasOnlyOneSameStation(Section section) {
        return !hasAllSameStations(section)
                && hasSameUpStation(section)
                || hasSameDownStation(section);
    }

    public boolean hasAllSameStations(Section section) {
        return hasSameUpStation(section) && hasSameDownStation(section);
    }

    public boolean hasSameUpStation(Section section) {
        return upStation.equals(section.upStation);
    }

    public boolean hasSameDownStation(Section section) {
        return downStation.equals(section.downStation);
    }

    public boolean hasSameDistance(Section newSection) {
        return distance.equals(newSection.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean upStationNameEqualsTo(String stationName) {
        return stationName.equals(upStation.getName());
    }

    public boolean downStationNameEqualsTo(String stationName) {
        return stationName.equals(downStation.getName());
    }
}
