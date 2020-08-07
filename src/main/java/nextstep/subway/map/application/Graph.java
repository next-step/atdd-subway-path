package nextstep.subway.map.application;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.PathResult;
import nextstep.subway.map.dto.SearchType;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Graph {

    public PathResult findPath(List<LineResponse> lines, Long start, Long target, SearchType type) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        setVertex(lines, graph);
        setEdge(lines, graph, type);

        GraphPath shortestPath = getGraphPath(start, target, graph);

        if (shortestPath == null) {
            throw new RuntimeException();
        }

        return new PathResult(shortestPath.getVertexList(), shortestPath.getWeight());
    }

    private GraphPath getGraphPath(Long start, Long target, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(start, target);
    }

    private void setVertex(List<LineResponse> lines, WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .map(it -> it.getStation())
                .collect(Collectors.toSet())
                .forEach(it -> graph.addVertex(it.getId()));
    }

    private void setEdge(List<LineResponse> lines, WeightedMultigraph<Long, DefaultWeightedEdge> graph, SearchType type) {
        if (type == null) {
            throw new RuntimeException();
        }

        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .filter(it -> it.getPreStationId() != null)
                .forEach(it -> graph.setEdgeWeight(
                        graph.addEdge(it.getPreStationId(), it.getStation().getId()), type.match(it)
                ));
    }
}
