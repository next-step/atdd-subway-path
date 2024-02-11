package nextstep.subway.applicaion.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Station;

public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

    public PathResponse() {
    }

    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations.stream().map(StationResponse::new).collect(Collectors.toList());
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
