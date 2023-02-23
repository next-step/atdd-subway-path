package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
    private List<StationResponse> stationResponse;
    private int distance;

    public PathResponse(List<StationResponse> stationResponse, int distance) {
        this.stationResponse = stationResponse;
        this.distance = distance;
    }

    public List<StationResponse> getStationResponse() {
        return stationResponse;
    }

    public int getDistance() {
        return distance;
    }

    public static PathResponse of(List<StationResponse> stations, int distance) {
        return new PathResponse(stations, distance);
    }
}
