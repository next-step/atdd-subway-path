package nextstep.subway.domain.path;

import nextstep.subway.domain.station.Station;

import java.util.List;

public class Path {
    private List<Station> stations;
    private double distance;

    public Path(List<Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}
