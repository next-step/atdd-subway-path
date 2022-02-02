package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
    private List<PathStationResponse> stations;
    private int distance;

    public List<PathStationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public PathResponse(List<PathStationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
