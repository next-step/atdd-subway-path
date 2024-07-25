package nextstep.subway.application.dto;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.domain.model.Path;
import nextstep.subway.domain.model.Station;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public PathResponse(List<Station> stations, int distance) {
        this.stations = stations.stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public static PathResponse from(Path path) {
        return new PathResponse(path.getStations(), path.getDistance());
    }
}

