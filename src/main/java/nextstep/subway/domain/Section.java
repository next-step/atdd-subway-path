package nextstep.subway.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.subway.applicaion.exception.BusinessException;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
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
    }

    private Section(Station upStation, Station downStation, int distance) {
        if (distance < 0) {
            throw new BusinessException();
        }
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public void updateLine(Line line) {
        this.line = line;
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

    public boolean isSameDownStation(Long stationId) {
        return downStation.isSameStation(stationId);
    }

    public boolean isSameUpStation(Long stationId) {
        return upStation.isSameStation(stationId);
    }

    public boolean isSameUpStation(Station station) {
        return upStation.isSameStation(station.getName()) || upStation.isSameStation(station.getId());
    }

    public boolean isSameDownStation(Station station) {
        return downStation.isSameStation(station.getName()) || downStation.isSameStation(station.getId());
    }
}
