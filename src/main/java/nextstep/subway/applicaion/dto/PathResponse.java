package nextstep.subway.applicaion.dto;

import java.util.ArrayList;
import java.util.List;

public class PathResponse {
    private List<StationResponse> stations = new ArrayList<>();
    private int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
