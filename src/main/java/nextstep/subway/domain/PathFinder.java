package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.exception.NotConnectedException;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.exception.SameStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public static PathFinder of(final List<Line> lines) {
        return new PathFinder(lines);
    }

    private PathFinder(final List<Line> lines) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        this.dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        drawGraph(lines);
    }

    private void drawGraph(final List<Line> lines) {
        lines.forEach(line -> addVertex(line.getStations()));
        lines.forEach(line -> addEdgeWeight(line.getSections()));
    }

    private void addVertex(final List<Station> stations) {
        stations.forEach(graph::addVertex);
    }

    private void addEdgeWeight(final Sections sections) {
        sections.getSections().forEach(s ->
            graph.setEdgeWeight(graph.addEdge(s.getUpStation(), s.getDownStation()), s.getDistance())
        );
    }

    public PathResponse findShortestPath(final Station source, final Station target) {
        return getShortestPathOrElseThrow(source, target);
    }

    private PathResponse getShortestPathOrElseThrow(final Station source, final Station target) {
        if(source.equals(target)) {
            throw new SameStationException();
        }

        if(isNotFound(source, target)) {
            throw new NotFoundStationException();
        }

        GraphPath<Station, DefaultWeightedEdge> findPath = dijkstraShortestPath.getPath(source, target);
        if(findPath == null) {
            throw new NotConnectedException();
        }

        int distance = (int) findPath.getWeight();
        List<Station> stations = findPath.getVertexList();
        return PathResponse.of(stations, distance);
    }

    private boolean isNotFound(final Station source, final Station target) {
        return !graph.containsVertex(source) || !graph.containsVertex(target);
    }
}
