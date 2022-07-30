package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private Distance distance;

    public Section() {

    }

    protected Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
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

    public Distance getDistance() {
        return distance;
    }

    public boolean downStationMatchFromUpStation(Section otherSection) {
        return downStationMatchFromUpStation(otherSection.upStation);
    }

    public boolean downStationMatchFromUpStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean upStationMatchFromDownStation(Section otherSection) {
        return upStationMatchFromDownStation(otherSection.downStation);
    }

    public boolean upStationMatchFromDownStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean upStationMatchFromStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean downStationMatchFromStation(Station station) {
        return this.downStation.equals(station);
    }

    public Section divide(Line line, Station upStation, Station downStation, int distance) {
        this.distance = this.distance.subtract(distance);
        if(this.upStation.equals(upStation)) {
            this.upStation = downStation;
            return new Section(line, upStation, downStation, distance);
        }
        this.downStation = upStation;
        return new Section(line, upStation, downStation, distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation);
    }
}