package nextstep.subway.domain;

import java.util.List;

public class Path {

    private List<Station> stations;
    private double distance;

    public Path(List<Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return this.stations;
    }

    public double getDistance() {
        return this.distance;
    }
}
