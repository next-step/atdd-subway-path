package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.path.StationNotRegisteredException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {

    public static PathResponse findShortestPath(final List<Line> lines, final Station source, final Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertexes(graph, lines);
        addEdges(graph, lines);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        try {
            final GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);
            return PathResponse.of(path);
        } catch (IllegalArgumentException e) {
            throw new StationNotRegisteredException();
        }
    }

    private static void addEdges(final WeightedMultigraph<Station, DefaultWeightedEdge> graph, final List<Line> lines) {
        lines.stream()
                .map(Line::getSections)
                .flatMap(sections -> sections.getOrderedSections().stream())
                .collect(Collectors.toList())
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private static void addVertexes(final WeightedMultigraph<Station, DefaultWeightedEdge> graph, final List<Line> lines) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .collect(Collectors.toList())
                .forEach(graph::addVertex);
    }
}
