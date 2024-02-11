package nextstep.subway.path;

import nextstep.subway.station.Station;

import java.util.List;

public class Path {
    private final List<Station> stations;
    private final Long distance;

    public Path(List<Station> stations,
                Long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public Path(List<Station> stations,
                Double distance) {
        this(stations, Math.round(distance));
    }

    public List<Station> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
