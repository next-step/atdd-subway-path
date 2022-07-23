package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public PathFinder(Lines lines) {
        addStationToGraphVertex(lines.getStations());
        addSectionToEdgeAndSetWeight(lines.getSections());
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station sourceStation, Station targetStation) {
        validInputCheck(sourceStation, targetStation);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        isConnectedPath(shortestPath);
        return shortestPath;
    }

    private void validInputCheck(final Station sourceStation, final Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new IllegalArgumentException("출발역과 도착역은 다른 역으로 지정되어야 합니다.");
        }
    }

    private void isConnectedPath(GraphPath<Station, DefaultWeightedEdge> shortestPath) {
        if (shortestPath == null) {
            throw new IllegalArgumentException("출발역과 도착역은 서로 연결이 되어있어야 합니다.");
        }
    }

    private void addStationToGraphVertex(List<Station> stations) {
        stations.forEach(graph::addVertex);
    }

    private void addSectionToEdgeAndSetWeight(List<Section> sections) {
        sections.forEach(section ->
                graph.setEdgeWeight(
                        graph.addEdge(
                                section.getUpStation(),
                                section.getDownStation()
                        ), section.getDistance().getDistance())
        );
    }
}
