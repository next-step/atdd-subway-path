package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
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

    public PathResult findPath(List<Line> lines, Long start, Long target) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        setVertex(lines, graph);
        setEdge(lines, graph);

        GraphPath shortestPath = getGraphPath(start, target, graph);

        return new PathResult(shortestPath.getVertexList(), shortestPath.getWeight());
    }

    private GraphPath getGraphPath(Long start, Long target, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(start, target);
    }

    private void setVertex(List<Line> lines, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        lines.stream()
                .flatMap(it -> it.getStationInOrder().stream())
                .map(it -> it.getStationId())
                .collect(Collectors.toSet())
                .forEach(it -> graph.addVertex(it));
    }

    private void setEdge(List<Line> lines, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        lines.stream()
                .flatMap(it -> it.getStationInOrder().stream())
                .filter(it -> it.getPreStationId() != null)
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getPreStationId(), it.getStationId()), it.getDistance()));
    }

}
