package nextstep.subway.applicaion.dto;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PathResponse {
    private int distance;
    private List<StationResponse> stations;

    public PathResponse(final int distance, final List<StationResponse> stations) {
        this.distance = distance;
        this.stations = stations;
    }

    public static PathResponse of(ShortestPathResult path) {
        return new PathResponse(path.getDistance(), createStationResponses(path));
    }

    private static List<StationResponse> createStationResponses(ShortestPathResult path) {
        return path.getStations().stream()
                   .map(StationResponse::of)
                   .collect(Collectors.toList());
    }
}
