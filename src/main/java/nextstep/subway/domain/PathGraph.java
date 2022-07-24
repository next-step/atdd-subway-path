package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class PathGraph {
    private final WeightedGraph<Station, DefaultWeightedEdge> graph;
    private final ShortestPathAlgorithm<Station, DefaultWeightedEdge> shortestPath;

    public PathGraph(List<Section> sections) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }

        this.shortestPath = new DijkstraShortestPath<>(graph);

    }

    public StationPath getShortestPath(Station source, Station target) {
        if (notExistVertex(source, target)) {
            throw new IllegalArgumentException("경로를 찾을 수 없어요.");
        }
        GraphPath<Station, DefaultWeightedEdge> path = Optional.ofNullable(shortestPath.getPath(source, target))
                .orElseThrow(() -> new IllegalArgumentException("경로를 찾을 수 없어요."));

        return new StationPath(path.getVertexList(), (int) path.getWeight());
    }

    private boolean notExistVertex(Station source, Station target) {
        return !(graph.containsVertex(source) && graph.containsVertex(target));
    }
}
