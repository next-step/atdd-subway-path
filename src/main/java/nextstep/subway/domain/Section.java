package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }


    public void updateUpStationToSectionDownStation(Section newSection) {
        if (isUnavailableUpdateDistance(newSection)) {
            throw new IllegalArgumentException("기존 구간의 길이보다 작아야해요");
        }

        this.distance = this.distance - newSection.distance;
        this.upStation = newSection.downStation;
    }

    public void updateDownStationToSectionUpStation(Section newSection) {
        if (isUnavailableUpdateDistance(newSection)) {
            throw new IllegalArgumentException("기존 구간의 길이보다 작아야해요");
        }

        this.distance = this.distance - newSection.distance;
        this.downStation = newSection.upStation;
    }

    public void updateDownStationToSectionDownStation(Section section) {
        this.distance = this.distance + section.distance;
        this.downStation = section.downStation;
    }

    public boolean equalsUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean equalsDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean anyEqualsStation(Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    private boolean isUnavailableUpdateDistance(Section newSection) {
        return this.distance <= newSection.distance;
    }

}