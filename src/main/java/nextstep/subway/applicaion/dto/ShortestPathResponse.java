package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.ShortestPath;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class ShortestPathResponse {
    private List<StationResponse> stations;
    private int distance;

    private ShortestPathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static ShortestPathResponse from(ShortestPath<Station, Integer> shortestPath) {
        return new ShortestPathResponse(
                shortestPath.getPaths().stream()
                        .map(StationResponse::fromEntity)
                        .collect(Collectors.toList()),
                shortestPath.getDistance()
        );
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
