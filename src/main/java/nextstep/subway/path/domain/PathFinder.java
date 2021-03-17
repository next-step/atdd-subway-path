package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {
    private WeightedMultigraph<Station, Integer> graph;

    public PathFinder(WeightedMultigraph<Station, Integer> graph) {
        this.graph = graph;
    }

    public List<Station> getShortestPath(Station 강남역, Station 교대역) {
        return null;
    }
}
