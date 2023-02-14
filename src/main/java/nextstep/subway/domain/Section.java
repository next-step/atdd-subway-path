package nextstep.subway.domain;

import javax.persistence.*;

import static nextstep.subway.common.constants.ErrorConstant.MORE_THEN_DISTANCE;

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

    public boolean isContainStation(Station station) {
        return upStation.equals(station)
                || downStation.equals(station);
    }

    public Section addStation(Station station, int distance) {
        this.distance = remainDistance(distance);
        Station downStation = this.downStation;
        this.downStation = station;

        return new Section(line, station, downStation, distance);
    }

    private int remainDistance(int distance) {
        if(distance <= 0 || this.distance <= distance){
            throw new IllegalArgumentException(MORE_THEN_DISTANCE);
        }

        return this.distance - distance;
    }

    public void changeUpStation(Station station) {
        upStation = station;
    }

    public Section() {

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