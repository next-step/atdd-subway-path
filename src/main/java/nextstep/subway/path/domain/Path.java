package nextstep.subway.path.domain;

import java.util.List;
import nextstep.subway.path.exception.PathInvalidDistanceException;
import nextstep.subway.path.exception.PathNotFoundException;
import nextstep.subway.station.domain.Station;

public class Path {
    private static int MIN_DISTANCE = 1;

    private final List<Station> stations;
    private final int distance;

    public Path(List<Station> stations, int distance) {
        if (stations.isEmpty()) {
            throw new PathNotFoundException();
        }
        if (distance < MIN_DISTANCE) {
            throw new PathInvalidDistanceException();
        }
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
