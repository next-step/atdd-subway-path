package nextstep.subway.applicaion.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PathResponse {

    private final List<StationResponse> stations;
    private final int distance;

    public PathResponse(List<StationResponse> stationResponse, int distance) {
        this.stations = stationResponse;
        this.distance = distance;
    }

    public static PathResponse of(List<StationResponse> stationResponse, int distance) {
        return new PathResponse(stationResponse, distance);
    }
}
