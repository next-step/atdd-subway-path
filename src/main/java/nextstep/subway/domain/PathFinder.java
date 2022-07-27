package nextstep.subway.domain;

import java.util.List;

public class PathFinder {
    SubwayGraph graph;

    public PathFinder(SubwayGraph graph) {
        this.graph = graph;
    }

    public List<Station> getShortestPath(Station source, Station target) {
        return graph.getShortestPath(source, target);
    }

    public int getShortestDistance(Station source, Station target) {
        return graph.getShortestDistance(source, target);
    }
}
