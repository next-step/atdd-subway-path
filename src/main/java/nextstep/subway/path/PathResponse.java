package nextstep.subway.path;

import nextstep.subway.station.Station;
import nextstep.subway.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public PathResponse() {}

    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
