package nextstep.subway.applicaion.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

    public PathResponse() {
    }

    public PathResponse(List<StationResponse> stations, Double distance) {
        this.stations = stations;
        this.distance = distance.intValue();
    }
}
