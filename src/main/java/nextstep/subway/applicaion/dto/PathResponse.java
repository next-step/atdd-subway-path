package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Path;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse from(Path path) {
        return new PathResponse(
                path.stations().stream()
                        .map(StationResponse::from)
                        .collect(Collectors.toList()),
                path.totalDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
