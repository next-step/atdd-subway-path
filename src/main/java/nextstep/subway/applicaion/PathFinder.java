package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PathFinder {

    public GraphPath<Long, DefaultWeightedEdge> getPath(List<Line> lines, Station sourceStation, Station targetStation) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraph(lines);

        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Long, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation.getId(), targetStation.getId());
        validatePath(path);

        return path;
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraph(final List<Line> lines) {
        List<Section> sections = lines.stream()
                .flatMap(it -> it.getSections().stream())
                .collect(Collectors.toList());

        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertexes(sections, graph);
        addEdges(sections, graph);

        return graph;
    }

    private void addVertexes(final List<Section> sections, final WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        sections.stream()
                .map(it -> it.getUpStation().getId())
                .forEach(graph::addVertex);

        sections.stream()
                .map(it -> it.getDownStation().getId())
                .forEach(graph::addVertex);
    }

    private void addEdges(final List<Section> sections, final WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        sections.forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getUpStation().getId(), it.getDownStation().getId()), it.getDistance()));
    }

    private void validatePath(final GraphPath<Long, DefaultWeightedEdge> path) {
        if (Objects.isNull(path)) {
            throw new IllegalArgumentException();
        }
    }
}
