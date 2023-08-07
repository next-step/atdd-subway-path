package nextstep.subway.path;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class ShortestPath<V> {
    private final List<V> vertexList;
    private final Integer shortestDistance;
    private final boolean isFound;

    public ShortestPath(WeightedMultigraph<V, DefaultWeightedEdge> graphPath, V source, V target) {
        var shortestPath = new DijkstraShortestPath<>(graphPath);
        var path = shortestPath.getPath(source, target);
        isFound = path != null;
        vertexList = isFound ? path.getVertexList() : List.of();
        shortestDistance = isFound ? (int) shortestPath.getPathWeight(source, target) : 0;
    }

    public List<V> getVertexList() {
        return vertexList;
    }

    public Integer getShortestDistance() {
        return shortestDistance;
    }

    public boolean isFound() {
        return isFound;
    }
}
