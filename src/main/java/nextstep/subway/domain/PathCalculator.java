package nextstep.subway.domain;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.exception.IllegalPathException;

public class PathCalculator {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> pathGraph;

    public PathCalculator(List<Line> allLines) {
        this.pathGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addAllLinesToPath(allLines);
    }

    public Path getShortestPath(Station sourceStation, Station targetStation) {
        validateStationsAreDifferent(sourceStation, targetStation);

        validateStationsInPath(sourceStation, targetStation);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(pathGraph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);

        List<Station> stations = path.getVertexList();
        int shortestDistance = (int) path.getWeight();

        return Path.of(stations, shortestDistance);
    }

    private void addAllLinesToPath(List<Line> allLines) {
        allLines.stream()
                .map(Line::getSections)
                .flatMap(sections -> sections.getSections().stream())
                .distinct()
                .forEach(section -> {
                    pathGraph.addVertex(section.getUpStation());
                    pathGraph.addVertex(section.getDownStation());
                    pathGraph.setEdgeWeight(pathGraph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
                });
    }

    private void validateStationsInPath(Station sourceStation, Station targetStation) {
        if (!pathGraph.containsVertex(sourceStation) || !pathGraph.containsVertex(targetStation)) {
            throw new IllegalPathException("출발역 또는 도착역이 경로에 들어있지 않습니다");
        }
    }

    private void validateStationsAreDifferent(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new IllegalPathException("출발역과 도착역이 같습니다.");
        }
    }
}
