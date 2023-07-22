package nextstep.subway.path;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import nextstep.subway.station.view.StationResponse;

@Getter
public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

    public PathResponse(ShortestPath shortestPath) {
        this.stations = shortestPath.getStations()
                                    .stream()
                                    .map(StationResponse::new)
                                    .collect(Collectors.toList());

        this.distance = shortestPath.getDistance();
    }
}
