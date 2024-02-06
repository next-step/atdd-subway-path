package nextstep.subway.line.section;

import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private Long distance;

    protected Section() {
    }

    public Section(Station upStation,
                   Station downStation,
                   Long distance) {
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

    public Long calculateAddDistance(AddType addType,
                                     Long distance) {
        if(addType.isDistanceChange()) {
            return distance + this.distance;
        }
        return distance;
    }

    public Long calculateSubDistance(Long distance) {
        return Math.abs(distance - this.distance);
    }

    public boolean anyMatchUpStationOrDownStation(Section section) {
        return isSameUpStation(section.downStation) || isSameDownStation(section.downStation);
    }

    public boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isSameUpStationInputDownStation(Section section) {
        return this.upStation.equals(section.downStation);
    }

    public boolean isSameUpStationInputUpStation(Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean isSameDownStationInputUpStation(Section section) {
        return this.downStation.equals(section.upStation);
    }

    public void changeSectionFromToInput(Section section) {
        this.upStation = section.downStation;
        this.distance = calculateSubDistance(section.distance);
    }

    public boolean anyMatchUpStationAndDownStation(Section section) {
        return this.upStation.equals(section.upStation) && this.downStation.equals(section.downStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }

}
