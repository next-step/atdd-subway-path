package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section {

    private static final String DISTANCE_ERROR_MESSAGE = "기존 역 사이 길이보다 크거나 같으면 등록 불가능 합니다.";

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

    public boolean isDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void subtractDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException(DISTANCE_ERROR_MESSAGE);
        }
        this.distance -= distance;
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }

    public void addDistance(int distance) {
        this.distance += distance;
    }
}