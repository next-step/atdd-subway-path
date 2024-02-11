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

    public Long calculateAddDistance(ApplyType applyType,
                                     Long distance) {
        if (!applyType.isApplyMiddle()) {
            return distance + this.distance;
        }

        if (distance <= this.distance) {
            throw new IllegalArgumentException("중간에 추가되는 구간은 라인보다 길수 없습니다.");
        }
        return distance;
    }

    public Long calculateSubDistance(Long distance) {
        return Math.abs(distance - this.distance);
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

    public boolean isSameDownStationInputDownStation(Section section) {
        return this.downStation.equals(section.downStation);
    }

    public boolean isSameDownStationInputUpStation(Section section) {
        return this.downStation.equals(section.upStation);
    }

    public void changeSectionFromToInput(ApplyPosition applyPosition,
                                         Section section) {
        this.distance = calculateSubDistance(section.distance);
        changeSection(applyPosition, section);
    }

    private void changeSection(ApplyPosition applyPosition,
                               Section section) {
        if (applyPosition.addingFirst()) {
            this.upStation = section.downStation;
            return;
        }
        this.downStation = section.upStation;
    }

    public void changeDownStationFromToInputDownStation(Section section) {
        this.distance += section.distance;
        this.downStation = section.downStation;
    }

    public boolean anyMatchUpStationAndDownStation(Section section) {
        return this.upStation.equals(section.upStation) && this.downStation.equals(section.downStation);
    }

    public boolean anyMatchUpStationAndDownStation(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    public Long distance() {
        return this.distance;
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
