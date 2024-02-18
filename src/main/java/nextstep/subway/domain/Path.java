package nextstep.subway.domain;

import java.util.List;
import nextstep.subway.domain.exception.PathException;
import org.jgrapht.GraphPath;
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
        GraphPath path = dijkstraShortestPath.getPath(source, target);
        if (path == null) {
            // 서로 연결되지 않은 역이다
            throw new PathException.SourceTargetNotConnectedException();
        }
        this.shortestPath = path.getVertexList();
        this.distance = (int) dijkstraShortestPath.getPathWeight(source, target);
    }

    public int getDistance() {
        return distance;
    }

    public List<String> getShortestPath() {
        return shortestPath;
    }
}
