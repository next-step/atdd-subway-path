package nextstep.subway.domain.dto;

import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class PathResponse {

    private List<Station> stations = new ArrayList<>();
    private double distance;

    public PathResponse(List<Station> stations, double distance) {
        this.stations.addAll(stations);
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}
