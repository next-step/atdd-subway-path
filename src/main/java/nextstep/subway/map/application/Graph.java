package nextstep.subway.map.application;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.PathResult;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Graph {

    public PathResult findPath(List<LineResponse> lineResponses, Long start, Long target) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        lineResponses.stream()
                .flatMap(it -> it.getStations().stream())
                .map(it -> it.getStation())
                .collect(Collectors.toList())
                .forEach(it -> graph.addVertex(it.getId()));

        lineResponses.stream()
                .flatMap(it -> it.getStations().stream())
                .filter(it -> it.getPreStationId() != null)
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getPreStationId(), it.getStation().getId()), it.getDistance()));

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath result = dijkstraShortestPath.getPath(start, target);

        return new PathResult(result.getVertexList(), result.getWeight());
    }

}
