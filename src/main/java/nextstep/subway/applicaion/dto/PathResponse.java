package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Path;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;
    
    public static PathResponse from(Path path) {
        return new PathResponse(
                path.getStations()
                        .stream()
                        .map(it -> new StationResponse(it.getId(), it.getName()))
                        .collect(Collectors.toList()), 
                path.getDistance().getValue()
        );
    }

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
