package nextstep.subway.path.domain;

import nextstep.subway.exception.NoPathExistsException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PathMap {

    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath;

    private PathMap(List<Line> lines) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        final List<LineStation> lineStations = lines.stream()
                .flatMap(line -> line.getStationInOrder().stream())
                .collect(Collectors.toList());


        for (LineStation lineStation : lineStations) {
            graph.addVertex(lineStation.getStationId());
        }
        for (LineStation lineStation : lineStations) {
            if (Objects.isNull(lineStation.getPreStationId())) {
                continue;
            }
            graph.setEdgeWeight(graph.addEdge(lineStation.getPreStationId(), lineStation.getStationId()), lineStation.getDistance());
        }

        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public static PathMap of(List<Line> lines) {
        return new PathMap(lines);
    }

    public List<Long> findDijkstraShortestPath(Long startStationId, Long endStationId) {
        assertExistence(startStationId, endStationId);
        GraphPath<Long, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(startStationId, endStationId);
        assertConnected(path);

        return path.getVertexList();
    }

    private void assertConnected(GraphPath<Long, DefaultWeightedEdge> path) {
        if (Objects.isNull(path) || CollectionUtils.isEmpty(path.getVertexList())) {
            throw new NoPathExistsException();
        }
    }

    private void assertExistence(Long startStationId, Long endStationId) {
        if (!graph.containsVertex(startStationId) || !graph.containsVertex(endStationId)) {
            throw new NotFoundException("노선도 상에 존재하지 않는 지하철역 입니다");
        }
    }
}
