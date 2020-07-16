package nextstep.subway.path.domain;

import nextstep.subway.line.dto.LineResponse;
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
                .flatMap(lineResponse -> lineResponse.getAllStation().stream())
                .collect(Collectors.toList());

        initGraph(graph, lines, stationResponses, type);

        GraphPath<Long, DefaultWeightedEdge> graphPath = findPath(startStationId, endStationId, graph);
        return getResultWithGraphPath(stationResponses, graphPath);
    }

    private ShortestPathResult getResultWithGraphPath(List<StationResponse> stationResponses, GraphPath<Long, DefaultWeightedEdge> graphPath) {
        List<Long> vertexList = graphPath.getVertexList();

        List<StationResponse> orderedStationResponse = vertexList.stream()
                .map(stationId -> stationResponses.stream()
                        .filter(stationResponse -> stationResponse.getId().equals(stationId))
                        .findFirst().orElseThrow(RuntimeException::new)
                ).collect(Collectors.toList());

        return ShortestPathResult.withResult(orderedStationResponse, graphPath.getWeight());
    }

    private GraphPath<Long, DefaultWeightedEdge> findPath(Long startStationId, Long endStationId, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(startStationId, endStationId);
    }

    private void initGraph(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<LineResponse> lines, List<StationResponse> stationResponses, PathFindType type) {
        addVertices(graph, stationResponses);

        addEdges(graph, lines, type);
    }

    private void addEdges(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<LineResponse> lines, PathFindType type) {
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
    }

    private void addVertices(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<StationResponse> stationResponses) {
        stationResponses.forEach(stationResponse -> graph.addVertex(stationResponse.getId()));
    }
}
