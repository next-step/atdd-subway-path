package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

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

    public List<Station> getStations() {
        return Arrays.asList(this.upStation, this.downStation);
    }

    public boolean isUpstation(Station station) {
        return station.equals(upStation);
    }

    public boolean isDownStation(Station station) {
        return station.equals(downStation);
    }

    public Section addStation(Station station, int distance) {
        this.distance = this.distance - distance;
        Station downStation = this.downStation;
        this.downStation = station;

        return new Section(line, station, downStation, distance);
    }

    public void modifyUpstation(Section section) {
        upStation = section.upStation;
        distance += section.distance;
    }

    @Override
    public String toString() {
        return "Section{" +
                "upStation=" + upStation +
                ", downStation=" + downStation +
                '}';
    }
}
