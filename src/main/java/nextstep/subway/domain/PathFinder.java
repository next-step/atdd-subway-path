package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {
    private final DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .forEach(graph::addVertex);

        lines.stream()
                .forEach(line -> setEdgeWeight(graph, line.getSections()));

        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public List<Station> shortPathStations(Station source, Station target) {
        return dijkstraShortestPath.getPath(source, target)
                                    .getVertexList();
    }

    public int shortPathWeight(Station source, Station target) {
        return (int) dijkstraShortestPath.getPathWeight(source, target);
    }

    private void setEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}
