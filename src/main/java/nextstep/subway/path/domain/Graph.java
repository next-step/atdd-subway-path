package nextstep.subway.path.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class Graph {
    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph
            = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    public Graph(List<Long> vertexes, List<Edge> edges) {
        setVertexes(vertexes);
        setEdges(edges);
    }

    private void setEdges(List<Edge> edges) {
        edges.forEach(it -> {
            DefaultWeightedEdge edge = graph.addEdge(it.getUpStationId(), it.getDownStationId());
            graph.setEdgeWeight(edge, it.getDistance());
        });
    }

    private void setVertexes(List<Long> vertexes) {
        vertexes.forEach(graph::addVertex);
    }

    public Path findShortestPath(Long source, Long target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException();
        }
        
        GraphPath<Long, DefaultWeightedEdge> shortestPath
                = new DijkstraShortestPath<>(graph).getPath(source, target);

        List<Long> vertexes = shortestPath.getVertexList();
        int distance = (int) shortestPath.getWeight();
        return new Path(vertexes, distance);
    }
}
