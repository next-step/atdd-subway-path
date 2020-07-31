package nextstep.subway.map.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stationResponses;
    private int distance;

    public PathResponse() {
    }

    public PathResponse(List<StationResponse> stationResponses, int distance) {
        this.stationResponses = stationResponses;
        this.distance = distance;
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }

    public int getDistance() {
        return distance;
    }
}
