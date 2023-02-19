package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

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

    public static PathResponse of(final GraphPath<Station, DefaultWeightedEdge> path) {
        final List<StationResponse> stations = path.getVertexList()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        final int distance = (int) path.getWeight();

        return new PathResponse(stations, distance);
    }
}
