package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {
    private static final String EMPTY_LINES_ERROR_MESSAGE = "노선이 없습니다.";
    private static final String SAME_STATION_ERROR_MESSAGE = "출발역과 도착역이 동일합니다.";
    private static final String NOT_FOUND_STATION_ERROR_MESSAGE = "노선에 역이 존재하지 않습니다.";

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        validationLines(lines);

        graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        settingGraph(lines, graph);

        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public List<Station> shortPathStations(Station source, Station target) {
        validationStations(source, target);
        return dijkstraShortestPath.getPath(source, target)
                                    .getVertexList();
    }

    public int shortPathWeight(Station source, Station target) {
        validationStations(source, target);
        return (int) dijkstraShortestPath.getPathWeight(source, target);
    }

    private void validationLines(List<Line> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_LINES_ERROR_MESSAGE);
        }
    }

    private void validationStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(SAME_STATION_ERROR_MESSAGE);
        }

        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new IllegalArgumentException(NOT_FOUND_STATION_ERROR_MESSAGE);
        }
    }

    private void settingGraph(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        settingGraphVertices(lines, graph);
        settingGraphEdgeWeights(lines, graph);
    }

    private void settingGraphEdgeWeights(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
                .forEach(line -> setEdgeWeight(graph, line.getSections()));
    }

    private void settingGraphVertices(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .forEach(graph::addVertex);
    }

    private void setEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}
