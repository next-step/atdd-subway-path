package nextstep.subway.path.dto;

import nextstep.subway.path.domain.StationGraphPath;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public PathResponse() {}

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

    public static PathResponse of(StationGraphPath stationGraphPath) {
        return of(
            stationGraphPath.getVertexStations(),
            stationGraphPath.getDistance()
        );
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
