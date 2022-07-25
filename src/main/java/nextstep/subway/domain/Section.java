package nextstep.subway.domain;

import javax.persistence.*;
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

    private int distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
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

    public boolean isEnoughDistance(int distance) {
        return this.distance > distance;
    }

    public boolean matchUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean matchDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isBetweenSection(Section section) {
        return matchUpStation(section.upStation) || matchDownStation(section.downStation);
    }

    public boolean isLeafSection(Section section) {
        return matchUpStation(section.downStation) || matchDownStation(section.upStation);
    }

    public void changeDownSection(Section newSection) {
        this.downStation = newSection.upStation;
        this.distance = minusDistance(newSection.distance);
    }

    public void changeUpSection(Section newSection) {
        this.upStation = newSection.downStation;
        this.distance = minusDistance(newSection.distance);
    }

    public boolean matchStations(Section section) {
        return matchUpStation(section.upStation) && matchDownStation(section.downStation);
    }

    public void combine(Section nextSection) {
        this.downStation = nextSection.downStation;
        this.distance = plusDistance(nextSection.distance);
    }

    private int minusDistance(int distance) {
        return this.distance - distance;
    }

    private int plusDistance(int distance) {
        return this.distance + distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}