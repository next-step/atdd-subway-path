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

    public GraphPath<Station, DefaultWeightedEdge> getPath(List<Line> lines, Station sourceStation, Station targetStation) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = makeGraph(lines);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        validatePath(path);

        return path;
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> makeGraph(final List<Line> lines) {
        List<Section> sections = lines.stream()
                .flatMap(it -> it.getSections().stream())
                .collect(Collectors.toList());

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertexes(sections, graph);
        addEdges(sections, graph);

        return graph;
    }

    private void addVertexes(final List<Section> sections, final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.stream()
                .map(Section::getUpStation)
                .forEach(graph::addVertex);

        sections.stream()
                .map(Section::getDownStation)
                .forEach(graph::addVertex);
    }

    private void addEdges(final List<Section> sections, final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getUpStation(), it.getDownStation()), it.getDistance()));
    }

    private void validatePath(final GraphPath<Station, DefaultWeightedEdge> path) {
        if (Objects.isNull(path)) {
            throw new IllegalArgumentException();
        }
    }
}
