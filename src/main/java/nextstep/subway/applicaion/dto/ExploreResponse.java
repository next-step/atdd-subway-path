package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class ExploreResponse {
    private List<StationResponse> stations;
    private int distance;

    private ExploreResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static ExploreResponse from(List<Station> stations, int distance) {
        return new ExploreResponse(stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList()), distance);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
