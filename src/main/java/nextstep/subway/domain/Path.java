package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.dto.PathResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.IllegalPathException;

public class Path {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> pathGraph;

    public Path() {
        this.pathGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    }

    public PathResponse getShortestPath(List<Line> allLines, Station sourceStation, Station targetStation) {
        validateStationsAreDifferent(sourceStation, targetStation);
        addAllLinesToPath(allLines);

        validateStationsInPath(sourceStation, targetStation);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(pathGraph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);

        List<Station> stations = path.getVertexList();
        int shortestDistance = (int) path.getWeight();
        List<StationResponse> stationResponses = stations.stream().map(StationResponse::createStationResponse).collect(Collectors.toList());

        return PathResponse.of(stationResponses, shortestDistance);
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
