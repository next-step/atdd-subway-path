package nextstep.subway.applicaion.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PathResponse {
    private final int distance;
    private final List<StationResponse> stations;

    public static PathResponse of(ShortestPath path) {
        return new PathResponse(path.getDistance(), createStationResponses(path));
    }

    private static List<StationResponse> createStationResponses(ShortestPath path) {
        return path.getStations().stream()
                   .map(StationResponse::of)
                   .collect(Collectors.toList());
    }
}
