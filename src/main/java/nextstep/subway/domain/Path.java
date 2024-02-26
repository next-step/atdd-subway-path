package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.exception.IllegalPathException;

public class Path {
    private static final WeightedMultigraph<String, DefaultWeightedEdge> pathGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    public List<String> getStationNamesAlongPath(String sourceName, String targetName) {
        validatePath(sourceName, targetName);
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(pathGraph);
        GraphPath<String, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceName, targetName);
        return path.getVertexList();
    }

    public int getShortestDistanceBetweenStations(String sourceName, String targetName) {
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(pathGraph);
        GraphPath<String, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceName, targetName);
        return (int) path.getWeight();
    }

    public void addConnectionBetweenStations(String sourceName, String targetName, int distance) {
        pathGraph.addVertex(sourceName);
        pathGraph.addVertex(targetName);
        pathGraph.setEdgeWeight(pathGraph.addEdge(sourceName, targetName), distance);
    }

    private void validatePath(String sourceName, String targetName) {
        if (Objects.equals(sourceName, targetName)) {
            throw new IllegalPathException("출발역과 도착역이 같습니다.");
        }
    }
}
