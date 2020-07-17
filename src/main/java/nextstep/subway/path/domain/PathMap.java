package nextstep.subway.path.domain;

import nextstep.subway.exception.NoPathExistsException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.LineStation;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class PathMap {

    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath;

    private PathMap(WeightedMultigraph<Long, DefaultWeightedEdge> graph, DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath) {
        this.graph = graph;
        this.dijkstraShortestPath = dijkstraShortestPath;
    }

    public static PathMap ofDistance(List<LineStation> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for (LineStation lineStation : lines) {
            graph.addVertex(lineStation.getStationId());
        }
        for (LineStation lineStation : lines) {
            if (Objects.isNull(lineStation.getPreStationId())) {
                continue;
            }
            graph.setEdgeWeight(graph.addEdge(lineStation.getPreStationId(), lineStation.getStationId()), lineStation.getDistance());
        }
        return new PathMap(graph, new DijkstraShortestPath<>(graph));
    }

    public static PathMap ofDuration(List<LineStation> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for (LineStation lineStation : lines) {
            graph.addVertex(lineStation.getStationId());
        }
        for (LineStation lineStation : lines) {
            if (Objects.isNull(lineStation.getPreStationId())) {
                continue;
            }
            graph.setEdgeWeight(graph.addEdge(lineStation.getPreStationId(), lineStation.getStationId()), lineStation.getDuration());
        }
        return new PathMap(graph, new DijkstraShortestPath<>(graph));
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
