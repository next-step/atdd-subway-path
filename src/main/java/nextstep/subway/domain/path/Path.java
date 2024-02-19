package nextstep.subway.domain.path;

import nextstep.subway.domain.Station;

import java.util.List;

public class Path {
    private List<Station> stations;
    private int distance;

    public Path(List<Station> stations, int distance) {
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
