package nextstep.subway.path.application.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.path.domain.StationPaths;
import nextstep.subway.station.application.dto.StationResponse;

@Getter
public class PathResponse {
    private final List<StationResponse> stations;
    private final int distance;

    @Builder
    private PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse from(StationPaths stationPath) {
        List<StationResponse> stations =
            stationPath.getStations()
                       .stream()
                       .map(StationResponse::from)
                       .collect(Collectors.toList());
        return PathResponse.builder()
            .stations(stations)
            .distance(stationPath.getDistance()
                                 .getValue()
            )
            .build();
    }
}
