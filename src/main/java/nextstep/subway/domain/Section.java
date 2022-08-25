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

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public Section updateUpStation(Station upStation) {
        return new Section(this.id, this.line, upStation, this.downStation, this.distance);
    }

    public Section updateDownStation(Station downStation) {
        return new Section(this.id, this.line, this.upStation, downStation, this.distance);
    }

    public Section updateDistance(Integer distance) {
        return new Section(this.id, this.line, this.upStation, this.downStation, distance);
    }

    public Section update(Station upStation, Station downStation, Integer distance) {
        return new Section(this.id, this.line, upStation, downStation, distance);
    }

    public Boolean isEqualToUpStation(Station station) {
        return this.upStation.equals(station);
    }
}