package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;

import static java.util.stream.Collectors.*;

public class PathResponse {

    private List<StationResponse> stations;
    private double distance;

    private PathResponse() { }

    private PathResponse(List<Station> stations, double distance) {
        this.stations = stations.stream()
                .map(station -> StationResponse.of(station.getId(), station.getName()))
                .collect(toList());
        this.distance = distance;
    }

    public static PathResponse of(List<Station> stations, double distance) {
        return new PathResponse(stations, distance);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }

}
