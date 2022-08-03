package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.ShortestRoute;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class PathResponse {

    private List<StationResponse> stations;

    private Long distance;


    public PathResponse() {

    }

    public PathResponse(ShortestRoute shortestRoute) {
        this.stations = shortestRoute.routes().stream()
                .map(StationResponse::new)
                .collect(toList());
        this.distance = shortestRoute.distance().longValue();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
