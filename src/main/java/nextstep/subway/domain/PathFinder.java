package nextstep.subway.domain;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Collection;
import java.util.List;

public class PathFinder {

    private WeightedGraph<Station, DefaultWeightedEdge> graph;
    private ShortestPathAlgorithm<Station, DefaultWeightedEdge> pathAlgorithm;

    private PathFinder(WeightedGraph<Station, DefaultWeightedEdge> graph, ShortestPathAlgorithm<Station, DefaultWeightedEdge> pathAlgorithm, List<Line> lines) {
        this.graph = graph;
        this.pathAlgorithm = pathAlgorithm;
        initGraph(lines);
    }

    public static PathFinder of(WeightedGraph<Station, DefaultWeightedEdge> graph, ShortestPathAlgorithm<Station, DefaultWeightedEdge> pathAlgorithm, List<Line> lines) {
        return new PathFinder(graph, pathAlgorithm, lines);
    }

    private void initGraph(List<Line> lines) {
        addVertexToGraph(lines);
        addEdgeToGraph(lines);
    }

    private void addVertexToGraph(List<Line> lines) {
        lines.stream()
                .map(Line::allStations)
                .flatMap(Collection::stream)
                .distinct()
                .forEach(graph::addVertex);
    }

    private void addEdgeToGraph(List<Line> lines) {
        lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}