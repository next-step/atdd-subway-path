package nextstep.subway.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PathResponse {
    private List<StationResponse> stations;

    private double distance;

    @Builder
    public PathResponse(List<StationResponse> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
