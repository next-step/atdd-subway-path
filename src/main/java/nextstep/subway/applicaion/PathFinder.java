package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private final WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

    public PathFinder(List<Line> allLines) {
    }

    public List<String> explore(Station source, Station target) {
        return null;
    }
}
