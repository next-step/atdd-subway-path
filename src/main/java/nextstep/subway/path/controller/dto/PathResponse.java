package nextstep.subway.path.controller.dto;

import nextstep.subway.station.controller.dto.StationResponse;

import java.util.List;

/** 경로조회 응답 DTO */
public class PathResponse {

    private List<StationResponse> stations;

    private int distance;

    public PathResponse() {
    }

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
