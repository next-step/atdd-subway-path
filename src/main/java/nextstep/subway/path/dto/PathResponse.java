package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;

public class PathResponse {

    private List<StationResponse> stationResponseList = new ArrayList<>();
    private Double distance;

    public PathResponse() {}

    public PathResponse(List<StationResponse> stationResponseList, Double distance) {
        this.stationResponseList = stationResponseList;
        this.distance = distance;
    }

    public PathResponse of (List<StationResponse> stationResponseList, Double distance) {
        return new PathResponse(stationResponseList, distance);
    }

    public List<StationResponse> getStationResponseList() {
        return stationResponseList;
    }

    public Double getDistance() {
        return distance;
    }
}
