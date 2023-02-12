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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "distance"))
    private Distance distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        if (hasIdenticalStations(upStation, downStation)) {
            throw new IllegalArgumentException();
        }
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    private boolean hasIdenticalStations(Station upStation, Station downStation) {
        return (upStation != null && downStation != null) && upStation.equals(downStation);
    }

    public void updateUpStation(Station station) {
        this.upStation = station;
    }

    public void updateDownStation(Station station) {
        this.downStation = station;
    }

    public boolean hasUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean hasDownStation(Station station) {
        return downStation.equals(station);
    }

    public void increaseDistance(int value) {
        distance.increase(value);
    }

    public void decreaseDistance(int value) {
        distance.decrease(value);
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
        return distance.value();
    }
}
