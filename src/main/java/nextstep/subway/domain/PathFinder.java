package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Line> lines) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Line line : lines) {
            initLineVertices(line);
        }
        for (Line line : lines) {
            initLineEdges(line);
        }
    }

    public GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station source, Station target) {
        checkSameTargetAndSource(source, target);
        checkStationExistence(source, target);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(source, target);

        checkPathConnected(shortestPath);

        return shortestPath;
    }

    private void checkStationExistence(Station source, Station target) {
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new IllegalArgumentException(PathErrorMessage.FIND_PATH_STATION_NOT_EXIST.getMessage());
        }
    }

    private void checkPathConnected(GraphPath<Station, DefaultWeightedEdge> shortestPath) {
        if (shortestPath == null) {
            throw new IllegalArgumentException(PathErrorMessage.FIND_PATH_NOT_CONNECTED.getMessage());
        }
    }

    private void checkSameTargetAndSource(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(PathErrorMessage.FIND_PATH_SAME_TARGET_AND_SOURCE.getMessage());
        }
    }

    private void initLineEdges(Line line) {
        List<Section> sections = line.getSections();
        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            graph.setEdgeWeight(graph.addEdge(section.getDownStation(), section.getUpStation()), section.getDistance());
        }
    }

    private void initLineVertices(Line line) {
        List<Station> stations = line.getOrderedStations();
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }
}
