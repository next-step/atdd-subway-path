package nextstep.subway.domain;

import nextstep.subway.error.exception.InvalidValueException;
import nextstep.subway.error.exception.LinesEmptyException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.List;

public class PathFinder {
    private List<Line> lines = new ArrayList<>();

    public PathFinder(List<Line> lines) {
        this.lines = lines;
    }

    public List<Station> getShortestPaths(Station source, Station target) {
        if (isEmpty()) {
            throw new LinesEmptyException();
        }
        if (isNotValidSourceOrTarget(source, target)) {
            throw new InvalidValueException();
        }
        return calShortestPathes(source, target);
    }

    private boolean isEmpty() {
        return lines == null || lines.isEmpty();
    }

    private boolean isNotValidSourceOrTarget(Station source, Station target) {
        return source.equals(target);
    }

    private List<Station> calShortestPathes(Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = getDrawingGraph();
        validateGraph(graph, source, target);
        return getDijkstraShortestPath(graph, source, target);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> getDrawingGraph() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addStations(graph);
        addSections(graph);

        return graph;
    }

    private void addStations(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.forEach(line ->
                line.getStations().forEach(graph::addVertex));
    }

    private void addSections(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.forEach(line ->
                line.getSections().forEach(s ->
                        graph.setEdgeWeight(addSection(graph, s), s.getDistance())));
    }

    private DefaultWeightedEdge addSection(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        return graph.addEdge((section.getUpStation()), section.getDownStation());
    }

    private void validateGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station source, Station target) {
        if (!graph.containsVertex(source)) {
            throw new InvalidValueException(source.getId());
        }
        if (!graph.containsVertex(target)) {
            throw new InvalidValueException(target.getId());
        }
    }

    private List<Station> getDijkstraShortestPath(
            WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(source, target).getVertexList();
    }

    public int getPathDistance(Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = getDrawingGraph();
        validateGraph(getDrawingGraph(), source, target);
        return getDijkstraShortestWeight(graph, source, target);
    }

    private int getDijkstraShortestWeight(
            WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return (int) dijkstraShortestPath.getPathWeight(source, target);
    }
}
