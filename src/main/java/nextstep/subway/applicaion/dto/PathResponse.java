package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private int distance;
    private List<StationResponse> stations;

    private PathResponse() {
    }

    public PathResponse(int distance, List<StationResponse> stations) {
        this.distance = distance;
        this.stations = stations;
    }

    public static PathResponse toResponse(int distance, List<Station> stations) {
        return new PathResponse(
                distance,
                stations.stream()
                        .map(StationResponse::toResponse)
                        .collect(Collectors.toList()));
    }

    public int getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
