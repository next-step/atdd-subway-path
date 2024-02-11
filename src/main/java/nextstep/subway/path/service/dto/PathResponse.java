package nextstep.subway.path.service.dto;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private int distance;
    private List<StationResponse> stations;

    public PathResponse() {
    }

    public PathResponse(final int distance, final List<StationResponse> stations) {
        this.distance = distance;
        this.stations = stations;
    }

    public static PathResponse of(final List<Station> stations, final int distance) {
        return new PathResponse(distance, stations.stream().map(StationResponse::from).collect(Collectors.toList()));
    }

    public int getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
