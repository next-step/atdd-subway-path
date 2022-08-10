package nextstep.subway.applicaion.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PathResponse {

    private List<StationResponse> stations;
    private Integer distance;

    public PathResponse(List<StationResponse> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
