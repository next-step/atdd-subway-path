package nextstep.subway.domain;

import nextstep.subway.exception.EndStationsAreNotLinkedException;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.LinkedList;
import java.util.List;

public class ShortestPath {
    private final LinkedList<Long> vertexList;
    private final double weight;

    public ShortestPath(List<Long> vertexList, double weight) {
        this.vertexList = new LinkedList<>(vertexList);
        this.weight = weight;
    }

    public static ShortestPath of(GraphPath<Long, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new EndStationsAreNotLinkedException();
        }

        return new ShortestPath(
                path.getVertexList(),
                path.getWeight()
        );
    }

    public List<Long> getVertexList() {
        return vertexList;
    }

    public double getWeight() {
        return weight;
    }
}
