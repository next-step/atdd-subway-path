package nextstep.subway.domain;

import java.util.List;

public class PathFinder {
    GraphStrategy graph;

    public PathFinder(GraphStrategy graph) {
        this.graph = graph;
    }

    public List<Station> findShortestPath(Station upStation, Station downStation) {
        return graph.findShortestPath(upStation, downStation);
    }

    public int getShortestDistance(Station upStation, Station downStation) {
        return graph.getShortestDistance(upStation, downStation);
    }
}
