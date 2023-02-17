package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private final Path path;

    public PathResponse(Path path) {
        this.path = path;
    }

    public List<StationResponse> getStations() {
        return createStationResponse(path.getStations());
    }

    public int getDistance() {
        return path.getDistance().getValue();
    }

    public List<StationResponse> createStationResponse(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }
}
