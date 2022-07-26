package nextstep.subway.domain;

import java.util.List;

public class Path {
    GraphStrategy graph;

    public Path(GraphStrategy graph) {
        this.graph = graph;
    }

    public List<Station> findShortestPath(Station upStation, Station downStation) {
        return graph.findShortestPath(upStation, downStation);
    }

    public int getShortestDistance() {
        return graph.getShortestPathSize();
    }
}
