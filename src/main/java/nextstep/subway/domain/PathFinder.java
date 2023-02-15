package nextstep.subway.domain;

import java.util.Collection;
import java.util.List;
import nextstep.subway.domain.exception.PathFindException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

    private final DijkstraShortestPath path;

    public PathFinder(final List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        initGraph(lines, graph);
        this.path = new DijkstraShortestPath(graph);
    }

    private void initGraph(
            final List<Line> lines,
            final WeightedMultigraph<Station, DefaultWeightedEdge> graph
    ) {
        lines.stream()
                .peek(line -> line.getStations().forEach(graph::addVertex))
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> graph.setEdgeWeight(
                        graph.addEdge(section.getUpStation(), section.getDownStation()),
                        section.getDistance().value()
                ));
    }

    public GraphPath find(final Station source, final Station target) {
        try {
            GraphPath result = path.getPath(source, target);
            validateFindPathSuccess(result);
            return result;
        } catch (Exception e) {
            throw new PathFindException();
        }
    }

    private void validateFindPathSuccess(final GraphPath graphPath) {
        if (graphPath == null) {
            throw new PathFindException();
        }
    }
}
