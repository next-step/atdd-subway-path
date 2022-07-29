package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;

public class StationDijkstraShortestPath {

    private DijkstraShortestPath dijkstraShortestPath;
    private Station source;
    private Station target;

    public StationDijkstraShortestPath(StationWeightedMultigraph graph, Station source, Station target) {
        this.source = source;
        this.target = target;
        this.dijkstraShortestPath = new DijkstraShortestPath(graph.getGraph());
    }

    public List<Station> getVertexList() {
        return getPath().getVertexList();
    }

    public int getWeight() {
        return (int) getPath().getWeight();
    }

    private GraphPath getPath() {
        return dijkstraShortestPath.getPath(source, target);
    }

}
