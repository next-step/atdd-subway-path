package nextstep.subway.domain;

import nextstep.subway.application.dto.PathResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class PathFinder {

    public PathResponse findPath(final List<Line> lines, final Station sourceStation, final Station targetStation) {
        if (sourceStation.isSame(targetStation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "출발역과 도착역이 같습니다.");
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        lines.stream()
                .flatMap(l -> l.getStations().stream())
                .distinct()
                .forEach(station -> {
                    graph.addVertex(station);
                });

        lines.stream()
                .flatMap(l -> l.getSections().stream())
                .distinct()
                .forEach(section -> {
                    final DefaultWeightedEdge defaultWeightedEdge = graph.addEdge(section.getUpStation(), section.getDownStation());
                    graph.setEdgeWeight(defaultWeightedEdge, section.getDistance());
                });

        if (!graph.containsVertex(sourceStation) || !graph.containsVertex(targetStation)) {
            throw new IllegalArgumentException("그래프에 존재하지 않는 정점입니다.");
        }

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        final List<Station> vertexList = path.getVertexList();
        final double pathWeight = path.getWeight();

        return new PathResponse(vertexList, pathWeight);
    }
}
