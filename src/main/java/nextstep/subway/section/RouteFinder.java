package nextstep.subway.section;

import java.util.List;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteFinder {
    private final Logger log = LoggerFactory.getLogger(RouteFinder.class);

    private final DijkstraShortestPath shortestPath;

    private RouteFinder(final DijkstraShortestPath shortestPath) {
        this.shortestPath = shortestPath;
    }

    public List<Station> findShortestRoute(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new BusinessException("출발역과 도착역은 같을 수 없습니다");
        }

        try {
            return shortestPath.getPath(source, target).getVertexList();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.info(e.getMessage());
            throw new BusinessException("출발역과 도착역이 연결되지 않았습니다");
        }
    }

    public int totalDistance(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new BusinessException("출발역과 도착역은 같을 수 없습니다");
        }

        GraphPath path = shortestPath.getPath(source, target);

        return path.getEdgeList().stream()
            .mapToInt(edge -> (int) path.getGraph().getEdgeWeight(edge))
            .sum();
    }

    public static RouteFinder from(final List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        sections.stream().forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
        });
        sections.stream().forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        });

        return new RouteFinder(new DijkstraShortestPath(graph));
    }
}