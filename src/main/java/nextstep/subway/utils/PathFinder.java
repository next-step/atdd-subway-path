package nextstep.subway.utils;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
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

    public List<Station> getShortPath(Station source, Station target) {
        return dijkstraShortestPath.getPath(source, target)
                                    .getVertexList();
    }

    public int getShortPathWeight(Station source, Station target) {
        return (int) dijkstraShortestPath.getPathWeight(source, target);
    }

    private void setEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}
