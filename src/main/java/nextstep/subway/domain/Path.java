package nextstep.subway.domain;

import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.util.ShortestPath;
import nextstep.subway.exception.SubwayRuntimeException;
import nextstep.subway.exception.message.SubwayErrorCode;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class Path {

    private final DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath;

    public Path(List<Line> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        lines.stream()
                .flatMap(it -> it.getSectionList().stream())
                .forEach(it -> {
                    graph.addVertex(it.getUpStation().getId());
                    graph.addVertex(it.getDownStation().getId());
                    graph.setEdgeWeight(graph.addEdge(it.getUpStation().getId(), it.getDownStation().getId()), it.getDistance());
                });

        this.dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public ShortestPath findShortestPath(Long source, Long target) {
        if (Objects.equals(source, target)) {
            throw new SubwayRuntimeException(SubwayErrorCode.CAN_NOT_EQUAL_STATION.getMessage());
        }

        try {
            GraphPath<Long, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);
            return ShortestPath.of(path);
        } catch (Exception e) {
            throw new SubwayRuntimeException(SubwayErrorCode.NOT_FOUND_STATION.getMessage());
        }
    }
}
