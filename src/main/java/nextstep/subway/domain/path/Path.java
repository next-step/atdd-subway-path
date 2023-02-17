package nextstep.subway.domain.path;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Station;

import java.util.List;

public class Path {
    private final List<Station> stations;
    private final Distance distance;

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
