package nextstep.subway.domain;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @Embedded
    private Distance distance;

    public int getDistanceIntValue() {
        return distance.getValue();
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, Distance.of(distance));
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this(id, line, upStation, downStation, Distance.of(distance));
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

    public void changeSectionConditionBy(Section newSection) {
        distance.decreaseDistance(newSection.getDistance());

        if (isEqualToUpStation(newSection.getUpStation())) {
            changeUpStation(newSection.getDownStation());
        }

        if (isEqualToDownStation(newSection.getDownStation())) {
            changeDownStation(newSection.getUpStation());
        }
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void addDistance(Distance distance) {
        this.distance.addDistance(distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)  {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation);
    }

}