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

        setVertex(lineResponses, graph);
        setEdge(lineResponses, graph);

        GraphPath shortestPath = getGraphPath(start, target, graph);

        return new PathResult(shortestPath.getVertexList(), shortestPath.getWeight());
    }

    private GraphPath getGraphPath(Long start, Long target, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(start, target);
    }

    private void setVertex(List<LineResponse> lineResponses, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        lineResponses.stream()
                .flatMap(it -> it.getStations().stream())
                .map(it -> it.getStation())
                .collect(Collectors.toSet())
                .forEach(it -> graph.addVertex(it.getId()));
    }

    private void setEdge(List<LineResponse> lineResponses, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        lineResponses.stream()
                .flatMap(it -> it.getStations().stream())
                .filter(it -> it.getPreStationId() != null)
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getPreStationId(), it.getStation().getId()), it.getDistance()));
    }

}
