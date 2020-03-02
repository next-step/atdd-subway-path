package atdd.path.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class Graph {
    private List<Line> lines;

    public Graph(List<Line> lines) {
        this.lines = lines;
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Station> getShortestDistancePath(Long startId, Long endId) {
        return getPathStations(makeGraph(lines), startId, endId);
    }

    public int getEstimatedTime(Long startId, Long endId) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraph(lines);
        GraphPath<Long, DefaultWeightedEdge> path = new DijkstraShortestPath(graph).getPath(startId, endId);

        return path.getEdgeList().stream()
                .map(it -> findEdge(graph.getEdgeSource(it), graph.getEdgeTarget(it)))
                .mapToInt(Edge::getElapsedMinutes)
                .sum();
    }

    private Edge findEdge(Long edgeSource, Long edgeTarget) {
        return lines.stream()
                .flatMap(it -> it.getEdges().stream())
                .filter(it -> it.getSourceStation().equals(edgeSource) && it.getTargetStation().equals(edgeTarget))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraph(List<Line> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(it -> graph.addVertex(it.getId()));

        lines.stream()
                .flatMap(it -> it.getEdges().stream())
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getSourceStation().getId(), it.getTargetStation().getId()), it.getDistance()));
        return graph;
    }

    private List<Station> getPathStations(WeightedMultigraph<Long, DefaultWeightedEdge> graph, Long startId, Long endId) {
        GraphPath<Long, DefaultWeightedEdge> path = new DijkstraShortestPath(graph).getPath(startId, endId);

        return path.getVertexList().stream()
                .map(it -> findStation(it))
                .collect(Collectors.toList());
    }

    private Station findStation(Long stationId) {
        return lines.stream()
                .flatMap(it -> it.getStations().stream())
                .filter(it -> it.getId().equals(stationId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}