package nextstep.subway.applicaion.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PathResponse {

    private final List<StationResponse> stations;
    private final int distance;

    public PathResponse(final List<StationResponse> stations, final int distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
