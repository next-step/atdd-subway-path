package nextstep.subway.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import nextstep.subway.domain.exception.PathFindException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

@Component
public class PathFinder {

    private DijkstraShortestPath path;

    public void initGraph(final List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        lines.stream()
                .peek(line -> line.getStations().forEach(graph::addVertex))
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> setEdge(graph, section));

        this.path = new DijkstraShortestPath(graph);
    }

    private void setEdge(final WeightedMultigraph<Station, DefaultWeightedEdge> graph, final Section section) {
        graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance().value()
        );
    }

    public GraphPath find(final Station source, final Station target) {
        try {
            validateSourceAndTargetIsNotEqual(source, target);
            return Optional.ofNullable(this.path.getPath(source, target)).orElseThrow(PathFindException::new);
        } catch (IllegalArgumentException e) {
            throw new PathFindException();
        }
    }

    private void validateSourceAndTargetIsNotEqual(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역은 같을 수 없습니다.");
        }
    }
}
