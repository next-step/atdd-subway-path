package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
    }

    public boolean isEqualToUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isEqualToDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    public boolean isSameStation(Station station) {
        return isEqualToDownStation(station) || isEqualToUpStation(station);
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void decreaseDistance(Integer distance) {
        this.distance = this.distance - distance;
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