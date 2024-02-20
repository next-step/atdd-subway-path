package nextstep.subway.dto;

import java.util.List;

public class PathResponse {
    List<StationResponse> stationResponses;
    int distance;

    public List<StationResponse> getStations() {
        return stationResponses;
    }

    public int getDistance() {
        return distance;
    }
}
