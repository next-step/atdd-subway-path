package nextstep.subway.path.service;

import nextstep.subway.path.domain.WeightedEdge;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ShortestPathFinder<EDGE extends WeightedEdge<VERTEX>, VERTEX> {
    private final WeightedMultigraph<VERTEX, DefaultWeightedEdge> graph;

    public ShortestPathFinder() {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    }

    public Optional<GraphPath<VERTEX, DefaultWeightedEdge>> find(final List<EDGE> edges, VERTEX source, VERTEX target) {
        addEdges(edges);
        setWeight(edges);
        DijkstraShortestPath<VERTEX, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);
        return Optional.ofNullable(shortestPath.getPath(source, target));
    }

    private void setWeight(final List<EDGE> edges) {
        edges.forEach(it -> {
            DefaultWeightedEdge edge = graph.addEdge(it.getSource(), it.getTarget());
            graph.setEdgeWeight(edge, it.getWeight());
        });
    }

    private void addEdges(final List<EDGE> edges) {
        edges.stream()
                .flatMap(it -> Stream.of(it.getTarget(), it.getSource()))
                .distinct()
                .forEach(graph::addVertex);
    }

}
