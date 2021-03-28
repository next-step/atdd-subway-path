package nextstep.subway.path.dto;

import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class PathResponse {
    private List<Station> stations;
    private int distance;

    public PathResponse() {
        this.stations = new ArrayList<>();
    }

    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return this.stations;
    }

    public int getDistance() {
        return this.distance;
    }
}
