package nextstep.subway.infra;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.EntityNotFoundException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class DijkstraShortestPathFinder implements PathFinder {
    final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    final DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

    @Override
    public PathResponse find(List<Line> lines, Station source, Station target) {
        validatePathFinder(lines, source, target);

        lines.stream()
                .map(line -> line.getSections().getSections())
                .flatMap(Collection::stream)
                .forEach(section -> setGraph(section));

        GraphPath graphPath = dijkstraShortestPath.getPath(source, target);
        validateNotConnected(graphPath);
        return PathResponse.of(graphPath.getVertexList(), dijkstraShortestPath.getPathWeight(source, target));
    }

    private void setGraph(Section section) {
        graph.addVertex(section.getUpStation());
        graph.addVertex(section.getDownStation());
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    private void validatePathFinder(List<Line> lines, Station source, Station target) {
        if (Objects.equals(source, target)) {
            throw new IllegalArgumentException("The departure and arrival stations must not be the same.");
        }

        checkExistStationsInLines(lines, source, target);
    }

    private void validateNotConnected(GraphPath graphPath) {
        if (Objects.isNull(graphPath)) {
            throw new IllegalArgumentException("Unconnected station.");
        }
    }

    private void checkExistStationsInLines(List<Line> lines, Station source, Station target) {
        boolean isContainSource = lines.stream().anyMatch(line -> line.isContainsStation(source));
        boolean isContainTarget = lines.stream().anyMatch(line -> line.isContainsStation(target));

        if (isContainSource && isContainTarget) {
            return;
        }

        throw new EntityNotFoundException("This station");
    }
}
