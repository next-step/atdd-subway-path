package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder() {
    }

    public void init(List<Line> lines) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Line line : lines) {
            initLineVertices(line);
        }
        for (Line line : lines) {
            initLineEdges(line);
        }
    }

    public GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station source, Station target) {
        if (isEachStationSame(source, target)) {
            throw new IllegalArgumentException(PathErrorMessage.FIND_PATH_SAME_TARGET_AND_SOURCE.getMessage());
        }
        if (!isEachStationExistent(source, target)) {
            throw new IllegalArgumentException(PathErrorMessage.FIND_PATH_STATION_NOT_EXIST.getMessage());
        }

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(source, target);

        if (!isPathConnected(shortestPath)) {
            throw new IllegalArgumentException(PathErrorMessage.FIND_PATH_NOT_CONNECTED.getMessage());
        }

        return shortestPath;
    }

    private boolean isEachStationExistent(Station source, Station target) {
        return graph.containsVertex(source) & graph.containsVertex(target);
    }

    private boolean isPathConnected(GraphPath<Station, DefaultWeightedEdge> shortestPath) {
        return shortestPath != null;
    }

    private boolean isEachStationSame(Station source, Station target) {
        return source.equals(target);
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
