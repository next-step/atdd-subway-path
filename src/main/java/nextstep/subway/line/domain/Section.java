package nextstep.subway.line.domain;

import nextstep.subway.line.application.InvalidSectionFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;
    private int duration;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance, int duration) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.duration = duration;
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

    public int getDuration() {
        return duration;
    }

    public void updateUpStation(Station station, int newDistance, int newDuration) {
        if (this.distance < newDistance) {
            throw new InvalidSectionFoundException("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
        }
        if (this.duration < newDuration) {
            throw new InvalidSectionFoundException("역과 역 사이의 소요 시간보다 적은 시간을 입력해주세요");
        }

        this.upStation = station;
        this.distance -= newDistance;
        this.duration -= newDuration;
    }

    public void updateDownStation(Station station, int newDistance, int newDuration) {
        if (this.distance < newDistance) {
            throw new InvalidSectionFoundException("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
        }
        if (this.duration < newDuration) {
            throw new InvalidSectionFoundException("역과 역 사이의 소요 시간보다 적은 시간을 입력해주세요");
        }

        this.downStation = station;
        this.distance -= newDistance;
        this.duration -= newDuration;
    }
}
