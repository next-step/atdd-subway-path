package nextstep.subway.path.dto.response;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.station.dto.response.StationResponse;
import nextstep.subway.station.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class PathResponse {

    List<StationResponse> stations;

    Integer distance;

    public static PathResponse of(GraphPath<Station, DefaultWeightedEdge> path) {
        List<StationResponse> stations = path.getVertexList()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return PathResponse.builder()
                .stations(stations)
                .distance((int) path.getWeight())
                .build();
    }

}
