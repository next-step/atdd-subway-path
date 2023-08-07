package nextstep.subway.path;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class ShortestPath<V> {
    private final List<V> vertexList;
    private final Integer shortestDistance;

    public ShortestPath(WeightedMultigraph<V, DefaultWeightedEdge> graphPath, V source, V target) {
        var shortestPath = new DijkstraShortestPath<>(graphPath);
        vertexList = shortestPath.getPath(source, target).getVertexList();
        shortestDistance = (int) shortestPath.getPathWeight(source, target);
    }

    public List<V> getVertexList() {
        return vertexList;
    }

    public Integer getShortestDistance() {
        return shortestDistance;
    }
}
