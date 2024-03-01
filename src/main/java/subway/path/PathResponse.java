package subway.path;

import subway.station.Station;
import subway.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private Long distance;

    public PathResponse() {}

    public PathResponse(List<Station> stations, Long distance) {
        this.stations = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
