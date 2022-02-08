package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.ExploredResult;
import nextstep.subway.handler.validator.ExploreValidator;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

    public PathFinder(List<Line> allLines) {
        allLines.forEach(line -> line.addStationsInGraphForExplore(graph));
    }

    public ExploredResult explore(Station source, Station target) {
        ExploreValidator.validateStationsIsSame(source, target);

        GraphPath path = new DijkstraShortestPath(graph).getPath(source, target);
        ExploreValidator.validateNotFound(path);

        return ExploredResult.of(path.getVertexList(), path.getWeight());
    }
}
