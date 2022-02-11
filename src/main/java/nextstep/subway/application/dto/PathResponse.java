package nextstep.subway.application.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private List<StationResponse> stations;
    private double distance;

    public PathResponse(List<StationResponse> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse from(List<Station> stations, Double distance) {
        return new PathResponse(stations.stream().map(StationResponse::from).collect(Collectors.toList()), distance);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}
