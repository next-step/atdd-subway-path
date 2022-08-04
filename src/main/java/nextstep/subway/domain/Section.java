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

    public boolean equalsDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean notEqualsDownStation(Station station) {
        return !equalsDownStation(station);
    }

    public boolean equalsUpStation(Station station) {
        return upStation.equals(station);
    }

    public int minusDistance(Section section) {
        return distance - section.getDistance();
    }

    public int plusDistance(Section section) {
        return distance + section.getDistance();
    }

    public List<Station> getAllStations() {
        return Arrays.asList(upStation, downStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;

        Section section = (Section) o;

        if (getDistance() != section.getDistance()) return false;
        if (getId() != null ? !getId().equals(section.getId()) : section.getId() != null) return false;
        if (getLine() != null ? !getLine().equals(section.getLine()) : section.getLine() != null) return false;
        if (getUpStation() != null ? !getUpStation().equals(section.getUpStation()) : section.getUpStation() != null)
            return false;
        return getDownStation() != null ? getDownStation().equals(section.getDownStation()) : section.getDownStation() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getLine() != null ? getLine().getId().hashCode() : 0);
        result = 31 * result + (getUpStation() != null ? getUpStation().hashCode() : 0);
        result = 31 * result + (getDownStation() != null ? getDownStation().hashCode() : 0);
        result = 31 * result + getDistance();
        return result;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}