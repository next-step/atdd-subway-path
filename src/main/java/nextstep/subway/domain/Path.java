package nextstep.subway.domain;

import java.util.List;

public class Path {

    private List<Station> stations;
    private Distance distance;

    public Path(List<Station> stations, double distance) {
        this(stations, Distance.valueOf(distance));
    }

    public Path(List<Station> stations, Distance distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public Distance getDistance() {
        return distance;
    }
}
