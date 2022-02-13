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

    public void updateUpStation(Station downStation, int distance) {
        if (this.distance <= distance) {
            throw new RuntimeException("기존 노선의 길이보다 길다.");
        }
        this.upStation = downStation;
        this.distance -= distance;
    }

    public void updateDownStation(Station upStation) {
        this.downStation = upStation;
    }

    public void minusDistance(int distance){
        if (this.distance <= distance) {
            throw new RuntimeException("기존 노선의 길이보다 길다.");
        }
        this.distance -= distance;
    }

    public void plusDistance(int distance){
        this.distance += distance;
    }

    public boolean isSameDownStation(Station station) {
        return downStation.equalId(station);
    }

    public boolean isSameUpStation(Station station) {
        return upStation.equalId(station);
    }


    public boolean existStation(Station station) {
        long id = station.getId();
        return upStation.getId() == id || downStation.getId() == id;
    }
}