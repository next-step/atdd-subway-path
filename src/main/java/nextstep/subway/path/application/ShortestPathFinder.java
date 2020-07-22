package nextstep.subway.path.application;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.dto.ShortestPathResult;
import nextstep.subway.station.dto.StationResponse;

@Service
public class ShortestPathFinder {

    public ShortestPathResult findShortestPath(List<LineResponse> lines, Long startId, Long endId,
        ShortestPathSearchType type) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        List<StationResponse> stationResponses = extractStationResponseList(lines);
        initGraph(graph, lines, stationResponses, type);

        GraphPath<Long, DefaultWeightedEdge> graphPath = findPath(startId, endId, graph);
        return getResultWithGraphPath(stationResponses, graphPath);
    }

    private void initGraph(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<LineResponse> lines,
        List<StationResponse> stationResponses, ShortestPathSearchType type) {
        addVertices(graph, stationResponses);
        addEdges(lines, graph, type);
    }

    private List<StationResponse> extractStationResponseList(List<LineResponse> allLines) {
        return allLines.stream()
            .flatMap(lineResponse -> lineResponse.getStations().stream())
            .map(LineStationResponse::getStation)
            .distinct()
            .collect(Collectors.toList());
    }

    private void addVertices(WeightedMultigraph<Long, DefaultWeightedEdge> graph,
        List<StationResponse> stationResponses) {
        stationResponses.forEach(stationResponse -> graph.addVertex(stationResponse.getId()));
    }

    private void addEdges(List<LineResponse> allLines, WeightedMultigraph<Long, DefaultWeightedEdge> graph,
        ShortestPathSearchType type) {
        allLines.stream()
            .flatMap(lineResponse -> lineResponse.getStations().stream())
            .filter(lineStationResponse -> Objects.nonNull(lineStationResponse.getPreStationId()))
            .forEach(lineStationResponse -> {
                    if (type == ShortestPathSearchType.DISTANCE) {
                        setEdgeWeightAsDistance(lineStationResponse, graph);
                    }
                    if (type == ShortestPathSearchType.DURATION) {
                        setEdgeWeightAsDuration(lineStationResponse, graph);
                    }
                }
            );
    }

    private void setEdgeWeightAsDistance(LineStationResponse lineStationResponse,
        WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        int weight = lineStationResponse.getDistance();
        graph.setEdgeWeight(
            graph.addEdge(lineStationResponse.getPreStationId(), lineStationResponse.getStation().getId()), weight
        );
    }

    private void setEdgeWeightAsDuration(LineStationResponse lineStationResponse,
        WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        int weight = lineStationResponse.getDuration();
        graph.setEdgeWeight(
            graph.addEdge(lineStationResponse.getPreStationId(), lineStationResponse.getStation().getId()), weight
        );
    }

    private GraphPath<Long, DefaultWeightedEdge> findPath(Long startStationId, Long endStationId,
        WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(startStationId, endStationId);
    }

    private ShortestPathResult getResultWithGraphPath(List<StationResponse> stationResponses,
        GraphPath<Long, DefaultWeightedEdge> graphPath) {
        List<Long> vertexList = graphPath.getVertexList();

        List<StationResponse> orderedStationResponse = vertexList.stream()
            .map(stationId -> stationResponses.stream()
                .filter(stationResponse -> stationResponse.getId().equals(stationId))
                .findFirst()
                .orElseThrow(RuntimeException::new)
            ).collect(Collectors.toList());

        return ShortestPathResult.ofResult(orderedStationResponse, graphPath.getWeight());
    }
}
