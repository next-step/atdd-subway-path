package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private final List<StationResponse> stations;

    private final int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(List<Station> stations, double distance) {
        return new PathResponse(
            stations.stream()
            .map(StationResponse::from)
            .collect(Collectors.toList())
            , (int) distance
        );
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
