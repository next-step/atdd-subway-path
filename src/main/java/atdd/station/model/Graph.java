package atdd.station.model;

import atdd.station.model.entity.Edge;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class Graph {
    private List<Edge> edges;

    public Graph(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Long> shortestDistancePath(long startId, long endId) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraph();
        edges.forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getSourceStationId(), it.getTargetStationId()), it.getDistance()));

        return new DijkstraShortestPath(graph).getPath(startId, endId).getVertexList();
    }

    public List<Long> shortestTimePath(long startId, long endId) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraph();
        edges.forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getSourceStationId(), it.getTargetStationId()), it.getElapsedTime()));

        return new DijkstraShortestPath(graph).getPath(startId, endId).getVertexList();
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraph() {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        edges.stream()
                .flatMap(it -> it.getStationIds().stream())
                .forEach(it -> graph.addVertex(it));
        return graph;
    }
}
