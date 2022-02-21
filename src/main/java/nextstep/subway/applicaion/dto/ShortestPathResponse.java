package nextstep.subway.applicaion.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Station;

public class ShortestPathResponse {

    private final List<StationResponse> stations;
    private final double distance;

    public ShortestPathResponse(List<Station> stations, double distance) {
        this.stations = createStationResponses(stations);
        this.distance = distance;
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}
