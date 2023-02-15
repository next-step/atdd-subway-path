package nextstep.subway.applicaion.dto;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.domain.Path;

public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

    public PathResponse(Path path) {
        this.stations = path.getStations().stream()
            .map(StationResponse::new)
            .collect(Collectors.toList());
        this.distance = path.getDistance();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
