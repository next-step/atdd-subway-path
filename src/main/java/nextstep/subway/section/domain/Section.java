package nextstep.subway.section.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

    public boolean hasSameDownwardStation(Station station) {
        Station currentDownwardStation = stations.getDownStation();
        return currentDownwardStation.equals(station);
    }

    public Integer getDistance() {
        return distance;
    }
}
