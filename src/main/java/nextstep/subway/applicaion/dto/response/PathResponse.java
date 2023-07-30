package nextstep.subway.applicaion.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
