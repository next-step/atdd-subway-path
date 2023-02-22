package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.ShortestPath;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PathResponse {

    private final List<StationResponse> stations;
    private final int distance;

    private PathResponse(final List<StationResponse> stations, final int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse from(final ShortestPath shortestPath) {
        return new PathResponse(
                shortestPath.getStations().stream()
                        .map(station -> new StationResponse(station.getId(), station.getName()))
                        .collect(Collectors.toList()),
                (int) shortestPath.getTotalDistance()
        );
    }
}
