package nextstep.subway.path.application.dto;

import lombok.Getter;
import nextstep.subway.station.applicaion.dto.response.StationResponse;

import java.util.List;

@Getter
public class PathResponse {
    private final List<StationResponse> stations;
    private final int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
