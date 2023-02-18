package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.path.InvalidPathException;
import nextstep.subway.domain.exception.path.SourceAndTargetCannotBeSameException;
import nextstep.subway.domain.exception.path.StationNotRegisteredException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JGraphPathFinder implements PathFinder {

    public PathResponse findPath(final List<Line> lines, final Station source, final Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        settingGraph(graph, lines);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        try {
            final GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);
            validatePath(path);
            return PathResponse.of(path);
        } catch (IllegalArgumentException e) {
            throw new StationNotRegisteredException();
        }
    }

    private static void validatePath(final GraphPath<Station, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new InvalidPathException();
        }
        if (path.getWeight() <= 0.0) {
            throw new SourceAndTargetCannotBeSameException();
        }
    }

    private void settingGraph(final WeightedMultigraph<Station, DefaultWeightedEdge> graph, final List<Line> lines) {
        lines.stream()
                .map(Line::getSections)
                .flatMap(sections -> sections.getOrderedSections().stream())
                .forEach(section -> addSection(graph, section));

    }

    private void addSection(final WeightedMultigraph<Station, DefaultWeightedEdge> graph, final Section section) {
        final Station upStation = section.getUpStation();
        final Station downStation = section.getDownStation();
        final int distance = section.getDistance();

        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), distance);
    }
}
