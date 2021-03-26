package nextstep.subway.line.dto;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private final List<StationResponse> stations;
    private final int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(List<Station> stations, int distance) {
        return new PathResponse(
                stations.stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toList()),
                distance
        );
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}