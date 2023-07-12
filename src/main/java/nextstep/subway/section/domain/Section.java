package nextstep.subway.section.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

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

    public boolean hasStation(Station downStation) {
        return downStation.equals(upStation) || downStation.equals(this.downStation);
    }

    public void assignLine(Line line) {
        this.line = line;
    }

    public boolean downStationEqualsTo(Station station) {
        return downStation.equals(station);
    }

    public boolean downStationEqualsToUpStationOf(Section newSection) {
        return downStation.equals(newSection.upStation);
    }
}
