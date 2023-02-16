package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Distance;

import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;
    private Integer distance;

    public PathResponse() {
    }

    public PathResponse(List<StationResponse> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
