package nextstep.subway.line.domain;

import nextstep.subway.exception.ExistUpAndDownStationException;
import nextstep.subway.exception.InvalidSectionDistanceException;
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

    public Section() {
    }

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
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

    public boolean hasMatchStation(Section section) {
        if(upStation.equals(section.getUpStation()) && downStation.equals(section.downStation)) {
            throw new ExistUpAndDownStationException();
        }

        return upStation.equals(section.getUpStation())
                || downStation.equals(section.getDownStation());
    }

    public void updateDownStation(Station downStation, int distance) {
        this.upStation = downStation;
        this.distance = distance;
    }

    public void updateUpStation(Station upStation, int distance) {
        this.downStation = upStation;
        this.distance = distance;
    }

    public int calculateDistance(int newDistance) {
        final int distance = this.distance - newDistance;
        if(distance < 0) {
            throw new InvalidSectionDistanceException();
        }
        return distance;
    }
}
