package nextstep.subway.path;

import nextstep.subway.station.Station;

import java.util.List;

public class Path {

    private List<Station> stations;
    private Long distance;

    public Path(List<Station> stations, Long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
