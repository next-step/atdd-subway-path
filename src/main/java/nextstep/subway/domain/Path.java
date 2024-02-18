package nextstep.subway.domain;

import java.util.List;
import nextstep.subway.domain.exception.PathException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class Path {

    private List<String> shortestPath;
    private int distance;

    public Path(WeightedMultigraph<String, DefaultWeightedEdge> graph, String source,
        String target) {
        if (source.equals(target)) {
            throw new PathException.PathSourceTargetSameException();
        }
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new PathException.PathNotFoundException();
        }
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();
        int distance = (int) dijkstraShortestPath.getPathWeight(source, target);
        this.shortestPath = shortestPath;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public List<String> getShortestPath() {
        return shortestPath;
    }
}
