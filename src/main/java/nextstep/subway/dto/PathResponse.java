package nextstep.subway.dto;

import java.util.List;

public class PathResponse {
    List<StationResponse> stationResponses;
    int distance;

    public PathResponse(List<StationResponse> stationResponses, int distance) {
        this.stationResponses = stationResponses;
        this.distance = distance;
    }

    public static PathResponse of(List<StationResponse> stationResponses, int distance) {
        return new PathResponse(stationResponses, distance);
    }

    public List<StationResponse> getStations() {
        return stationResponses;
    }

    public int getDistance() {
        return distance;
    }
}
