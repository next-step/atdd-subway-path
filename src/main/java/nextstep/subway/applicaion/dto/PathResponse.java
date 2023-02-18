package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private final List<StationResponse> stationResponse;

    private final double distance;

    public List<StationResponse> getStationResponse() {
        return stationResponse;
    }

    public double getDistance() {
        return distance;
    }

    public PathResponse(List<Station> stationList, double distance) {
        this.stationResponse = stationList.stream().map(StationResponse::new).collect(Collectors.toList());
        this.distance = distance;
    }
}
