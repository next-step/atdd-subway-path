package nextstep.subway.domain;

import java.util.Collections;
import java.util.List;

public class Paths {

    private final List<Station> stations;
    private final int distance;

    public Paths(List<Station> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    public int getDistance() {
        return distance;
    }
}
