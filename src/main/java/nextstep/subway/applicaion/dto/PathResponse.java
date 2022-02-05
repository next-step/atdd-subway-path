package nextstep.subway.applicaion.dto;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.subway.domain.path.ShortestPath;
import nextstep.subway.domain.station.Station;

public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(List<Station> stations, int distance) {
        if (Objects.isNull(stations) || stations.isEmpty()) {
            stations = ImmutableList.of();
        }

        return new PathResponse(stations.stream()
            .map(StationResponse::from)
            .collect(Collectors.toList()),
            distance);
    }

    public static PathResponse of(ShortestPath shortestPath) {
        return PathResponse.of(shortestPath.get().stations(),
            shortestPath.totalDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
