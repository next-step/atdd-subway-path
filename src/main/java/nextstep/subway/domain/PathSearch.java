package nextstep.subway.domain;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathSearch {

    private WeightedMultigraph<String, DefaultWeightedEdge> graph;

    public PathSearch() {
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    }

    /**
     *
     */
    public void registerPaths(List<Line> lines) {

    }

    public void findShortestPath() {

    }

}
