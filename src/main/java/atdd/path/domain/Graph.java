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
        return getPathStations(makeGraph(lines, true), startId, endId);
    }

    public List<Station> getShortestTimePath(Long startId, Long endId) {
        return getPathStations(makeGraph(lines, false), startId, endId);
    }

    public int getEstimatedTime(Long startId, Long endId) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraph(lines, false);
        GraphPath<Long, DefaultWeightedEdge> path = new DijkstraShortestPath(graph).getPath(startId, endId);

        return path.getEdgeList().stream()
                .map(it -> findEdge(graph.getEdgeSource(it), graph.getEdgeTarget(it)))
                .mapToInt(Edge::getTimeToTake)
                .sum();
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraph(List<Line> lines, boolean isDistanceWeight) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(it -> graph.addVertex(it.getId()));

        lines.stream()
                .flatMap(it -> it.getEdges().stream())
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getSource().getId(), it.getTarget().getId()),
                        isDistanceWeight ? it.getDistance() : it.getTimeToTake()));
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

    private Edge findEdge(Long edgeSource, Long edgeTarget) {
        return lines.stream()
                .flatMap(it -> it.getEdges().stream())
                .filter(it -> it.getSource().getId().equals(edgeSource)
                        && it.getTarget().getId().equals(edgeTarget))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}