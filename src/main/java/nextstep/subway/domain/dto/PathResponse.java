package nextstep.subway.domain.dto;

import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class PathResponse {

    private List<Station> stations = new ArrayList<>();
    private int distance;

    public PathResponse(List<Station> stations, int distance) {
        this.stations.addAll(stations);
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
