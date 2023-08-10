package nextstep.subway.domain;

import java.util.List;

public class Path {

    private List<Station> stations;
    private Distance distance;

    public static Path create(List<Station> stations, int distance) {
        if (stations.size() < 2) {
            throw new IllegalArgumentException();
        }
        return new Path(stations, Distance.of(distance));
    }

    private Path(List<Station> stations, Distance distance) {
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
