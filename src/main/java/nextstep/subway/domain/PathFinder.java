package nextstep.subway.domain;

import nextstep.subway.application.dto.PathResponse;
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
                .forEach(station -> graph.addVertex(station));

        lines.stream()
                .flatMap(l -> l.getSections().stream())
                .forEach(section -> {
                    final DefaultWeightedEdge defaultWeightedEdge = graph.addEdge(section.getUpStation(), section.getDownStation());
                    graph.setEdgeWeight(defaultWeightedEdge, section.getDistance());
                });

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        final List<Station> vertexList = dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
        final double pathWeight = dijkstraShortestPath.getPathWeight(sourceStation, targetStation);
        
        return new PathResponse(vertexList, pathWeight);
    }
}
