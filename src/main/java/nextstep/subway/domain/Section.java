package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Section implements Comparable<Section> {
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
    private Distance distance;

    protected Section() {
    }

    private Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, Distance.from(distance));
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public int compareTo(Section section) {
        if (upStation.equals(section.getDownStation())) {
            return 1;
        }

        if (downStation.equals(section.getUpStation())) {
            return -1;
        }

        return 0;
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
    }

    public void upStationUpdate(Station downStation, Distance divideDistance) {
        distance.reduceDistance(divideDistance);
        this.upStation = downStation;
    }

    public void downStationUpdate(Station upStation, Distance divideDistance) {
        distance.reduceDistance(divideDistance);
        this.downStation = upStation;
    }
}