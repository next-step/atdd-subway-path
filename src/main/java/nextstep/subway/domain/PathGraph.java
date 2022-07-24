package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

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

        this.shortestPath = new DijkstraShortestPath(graph);

    }

    public StationPath getShortestPath(Station source, Station target) {
        if (notContainsEdge(source, target)) {
            throw new IllegalArgumentException("경로를 찾을 수 없어요.");
        }
        GraphPath<Station, DefaultWeightedEdge> path = shortestPath.getPath(source, target);
        return new StationPath(path.getVertexList(), (int) path.getWeight());
    }

    private boolean notContainsEdge(Station source, Station target) {
        return !graph.containsEdge(source, target);
    }
}
