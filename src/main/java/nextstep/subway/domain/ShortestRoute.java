package nextstep.subway.domain;

import java.util.List;

public class ShortestRoute {

    private List<Station> stations;
    private double distance;

    public ShortestRoute() {

    }

    public ShortestRoute(List<Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> routes() {
        return this.stations;
    }

    public Double distance() {
        return distance;
    }
}
