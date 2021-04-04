package nextstep.subway.path.domain.graph;

import nextstep.subway.path.domain.PathResult;
import nextstep.subway.station.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class DijkstraPath implements Path {

    private final DijkstraShortestPath dijkstraShortestPath;

    public DijkstraPath(WeightedMultigraph graph) {
        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    @Override
    public PathResult find(Station source, Station target) {
        List<Station> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();
        int distance = (int) dijkstraShortestPath.getPathWeight(source, target);
        return new PathResult(shortestPath, distance);
    }
}
