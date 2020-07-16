package nextstep.subway.path.domain;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathFinder {

    public ShortestPathResult findShortestPath(List<LineResponse> lines, Long startStationId, Long endStationId, PathFindType type) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        List<StationResponse> stationResponses = lines.stream()
                .flatMap(lineResponse -> lineResponse.getStations().stream())
                .map(LineStationResponse::getStation)
                .distinct()
                .collect(Collectors.toList());

        stationResponses.forEach(stationResponse -> graph.addVertex(stationResponse.getId()));

        lines.stream()
                .flatMap(lineResponse -> lineResponse.getStations().stream())
                .filter(lineStationResponse -> lineStationResponse.getPreStationId() != null)
                .forEach(lineStationResponse -> {
                            int weight = type == PathFindType.DISTANCE ? lineStationResponse.getDistance() : lineStationResponse.getDuration();
                            graph.setEdgeWeight(
                                    graph.addEdge(lineStationResponse.getPreStationId(), lineStationResponse.getStation().getId()), weight
                            );
                        }
                );

        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Long, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(startStationId, endStationId);
        List<Long> vertexList = graphPath.getVertexList();

        List<StationResponse> orderedStationResponse = vertexList.stream()
                .map(stationId -> stationResponses.stream()
                        .filter(stationResponse -> stationResponse.getId().equals(stationId))
                        .findFirst().orElseThrow(RuntimeException::new)
                ).collect(Collectors.toList());

        return ShortestPathResult.withResult(orderedStationResponse, graphPath.getWeight());
    }
}
