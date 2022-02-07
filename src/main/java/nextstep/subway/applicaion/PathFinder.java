package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.handler.validator.ExploreValidator;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private final WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    private int distance;

    public PathFinder(List<Line> allLines) {
        allLines.forEach(line -> line.getSections().pushSections(graph));
    }

    public List<String> explore(String source, String target) {
        ExploreValidator.validateStationsIsSame(source, target);

        GraphPath path = new DijkstraShortestPath(graph).getPath(source, target);
        ExploreValidator.validateNotFound(path);

        distance = (int) path.getWeight();
        return path.getVertexList();
    }

    public int exploreDistance() {
        return distance;
    }
}
