package nextstep.subway.applicaion;

import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.ShortestPathStrategy;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Dikstra implements ShortestPathStrategy<Station, Section> {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

    @Override
    public void init(List<Section> edges) {
        Set<Station> vertices = edges.stream()
                .map(section -> List.of(section.getUpStation(), section.getDownStation()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        vertices.forEach(graph::addVertex);
        for (Section section : edges) {
            var edge = graph.addEdge(section.getUpStation(), section.getDownStation());
            graph.setEdgeWeight(edge, section.getDistance());
        }
    }

    @Override
    public Optional<Path> shortestPath(Station source, Station target) {
        var path = dijkstraShortestPath.getPath(source, target);
        if (path == null) {
            return Optional.empty();
        }

        return Optional.of(new Path(path.getVertexList(), path.getWeight()));
    }

    @Override
    public Set<Station> allVertices() {
        return graph.vertexSet();
    }
}
