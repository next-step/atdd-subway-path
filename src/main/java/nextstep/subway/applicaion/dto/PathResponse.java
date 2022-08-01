package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.Station;

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

    public static PathResponse of(int distance, List<Station> path) {
        return new PathResponse(distance, createStationResponses(path));
    }

    private static List<StationResponse> createStationResponses(List<Station> path) {
        return path.stream()
                   .map(StationResponse::of)
                   .collect(Collectors.toList());
    }
}
