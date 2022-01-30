package nextstep.subway.domain;

import nextstep.subway.exception.section.MinimumDistanceException;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section {

    private static final int MIN_DISTANCE = 1;

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

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        Section result = new Section(upStation, downStation, distance);
        result.line = line;

        return result;
    }

    public void updateDistance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public String getUpStationName() {
        return upStation.getName();
    }

    public String getDownStationName() {
        return downStation.getName();
    }

    public Station getUpStation() {
        return upStation;
    }

    public int getDistance() {
        return distance;
    }

    public Station getDownStation() {
        return downStation;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    private void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new MinimumDistanceException(distance);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}