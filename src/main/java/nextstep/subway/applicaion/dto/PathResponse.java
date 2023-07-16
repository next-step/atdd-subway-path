package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private final List<StationResponse> stations;
    private final double distance;

    public PathResponse(List<Station> stations, double distance) {
        this.stations = stations.stream().map(StationResponse::from).collect(Collectors.toList());
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}
