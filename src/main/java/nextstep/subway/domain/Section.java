package nextstep.subway.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "section")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    @Column(name = "distance")
    private int distance;

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    protected Section() {

    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public static Section createMiddleSection(Section existingSection, Section newSection) {
        return Section.of(newSection.getDownStation(),
                          existingSection.getDownStation(),
                          existingSection.getDistance() - newSection.getDistance());
    }

    public boolean isSameWithDownStation(Station station) {
        return downStation.equals(station);
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


    public boolean isSameWithUpStation(Station station) {
        return upStation.equals(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public String toString() {
        return "Section{" +
               "id=" + id +
               ", upStation=" + upStation +
               ", downStation=" + downStation +
               ", distance=" + distance +
               '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
