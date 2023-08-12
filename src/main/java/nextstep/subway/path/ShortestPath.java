package nextstep.subway.path;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.List;

public class ShortestPath<V> {
    private final DijkstraShortestPath<V, DefaultWeightedEdge> shortestPath;
    private final V source;
    private final V target;

    public ShortestPath(WeightedMultigraph<V, DefaultWeightedEdge> graphPath, V source, V target) {
        shortestPath = new DijkstraShortestPath<>(graphPath);
        this.source = source;
        this.target = target;
    }


    public List<V> getVertexList() {
        if (!isFound()) {
            return List.of();
        }
        return shortestPath.getPath(source, target).getVertexList();
    }

    public Integer getShortestDistance() {
        if (!isFound()) {
            return 0;
        }
        return (int) shortestPath.getPath(source, target).getWeight();
    }

    public boolean isFound() {
        return shortestPath.getPath(source, target) != null;
    }
}
