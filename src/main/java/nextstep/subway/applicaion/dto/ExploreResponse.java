package nextstep.subway.applicaion.dto;

import java.util.List;
import java.util.stream.Collectors;

public class ExploreResponse {
    private List<StationResponse> stations;
    private int distance;

    private ExploreResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static ExploreResponse from(ExploredResult exploredResult) {
        List<StationResponse> stationResponses = exploredResult.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());

        return new ExploreResponse(stationResponses, exploredResult.getDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
