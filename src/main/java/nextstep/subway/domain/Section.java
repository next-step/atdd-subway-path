package nextstep.subway.domain;

import javax.persistence.*;
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

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }

    public boolean matchDownStation(Station station) {
        return downStation.equals(station);
    }

    public Boolean anyMatch(Section otherSection) {
        return getStations().stream()
                .anyMatch(otherSection.getStations()::contains);
    }

    public Boolean anyMatchStation(Station otherStation) {
        return getStations().stream().anyMatch(station -> station.equals(otherStation));
    }

    public void minusDistance(Distance distance) {
        this.distance = this.distance.minus(distance);
    }

    public void plusDistance(Distance distance) {
        this.distance = this.distance.plus(distance);
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation);
    }
}