package nextstep.subway.applicaion.dto;

import nextstep.subway.event.Path;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public PathResponse(Path path) {
        this(
                path.getStations().stream()
                        .map(StationResponse::new)
                        .collect(Collectors.toList()),
                path.getDistance()
        );
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
