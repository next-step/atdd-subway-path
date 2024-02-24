package nextstep.subway.path;

import nextstep.subway.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private List<StationResponse> stations;
    private Long distance;

    public PathResponse(Path path) {
        this.stations = path.getStations()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        this.distance = path.getDistance();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
