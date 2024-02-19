package nextstep.subway.domain;

import nextstep.subway.domain.exception.PathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class Path {

    private List<Long> vertexList;
    private int distance;

    public Path(List<Long> vertexList, int distance) {
        this.vertexList = vertexList;
        this.distance = distance;
    }

    public static Path shortestPath(WeightedMultigraph<Long, DefaultWeightedEdge> graph, Long source, Long target) {
        if (source.equals(target)) {
            throw new PathException.PathSourceTargetSameException();
        }
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new PathException.PathNotFoundException();
        }
        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Long, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);
        if (path == null) {
            // 서로 연결되지 않은 역이다
            throw new PathException.SourceTargetNotConnectedException();
        }
        return new Path(path.getVertexList(), (int) path.getWeight());
    }

    public int getDistance() {
        return distance;
    }

    public List<Long> getVertexList() {
        return vertexList;
    }
}
