package nextstep.subway.domain.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="down_station_id")
    private Station downStation;

    private int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(this.getUpStation());
        stations.add(this.getDownStation());
        return stations;
    }

    public boolean isSameAsUpStation(Station station) {
        return this.getUpStation().equals(station);
    }

    public boolean isSameAsDwonStation(Station station) {
        return this.getDownStation().equals(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(getUpStation(), section.getUpStation())
                && Objects.equals(getDownStation(), section.getDownStation())
                && getDistance() == section.getDistance();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUpStation(), getDownStation(), getDistance());
    }
}
