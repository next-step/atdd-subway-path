package nextstep.subway.section.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @Embedded
    private SectionStations stations;

    private Integer distance;

    protected Section() {}

    public Section(SectionStations stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public Section(Line line, SectionStations stations, Integer distance) {
        this.line = line;
        this.stations = stations;
        this.distance = distance;
    }

    public Station getDownwardStation() {
        return stations.getDownStation();
    }

    public Station getUpwardStation() {
        return stations.getUpStation();
    }

    public SectionStations getStations() {
        return stations;
    }

    public boolean checkStationInSection(Station station) {
        return stations.checkStationInSection(station);
    }

    public boolean hasSameUpwardStation(Station station) {
        Station currentUpwardStation = stations.getUpStation();
        return currentUpwardStation.equals(station);
    }

    public boolean hasSameDownwardStation(Station station) {
        Station currentDownwardStation = stations.getDownStation();
        return currentDownwardStation.equals(station);
    }

    public void insertDownwardInterStation(Section section) {
        checkValidDistance(section);

        SectionStations oldOne = new SectionStations(stations.getUpStation(), section.getDownwardStation());
        SectionStations newOne = new SectionStations(section.getDownwardStation(), stations.getDownStation());

        this.stations = oldOne;
        section.stations = newOne;

        int newDistance = this.distance - section.distance;
        section.distance = newDistance;
        this.distance -= newDistance;
    }

    public void insertUpwardInterStation(Section section) {
        checkValidDistance(section);

        SectionStations oldOne = new SectionStations(stations.getUpStation(), section.getUpwardStation());
        SectionStations newOne = new SectionStations(section.getUpwardStation(), stations.getDownStation());

        this.stations = oldOne;
        section.stations = newOne;

        this.distance -= section.distance;
    }

    private void checkValidDistance(Section section) {
        if (section.distance >= this.distance) {
            throw new CustomException(ErrorCode.INVALID_INTER_STATION_DISTANCE);
        }
    }

    public Integer getDistance() {
        return distance;
    }

    public void updateLine(Line line) {
        this.line = line;
    }
}
