package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    private PathResponse() {
    }

    public PathResponse(List<StationResponse> stations, double pathWeight) {
        this.stations = stations;
        this.distance = Double.valueOf(pathWeight).intValue();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
