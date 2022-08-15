package nextstep.subway.domain;

import java.util.List;

public class Stations {

    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getList() {
        return stations;
    }

    public void add(Station station) {
        this.stations.add(station);
    }

    public void add(Section section) {
        this.stations.add(section.getUpStation());
        this.stations.add(section.getDownStation());
    }
}
