package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Objects;

public class PathFinder {
    private static WeightedMultigraph<Station, DefaultWeightedEdge> buildGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .forEach(station -> graph.addVertex(station));

        lines.stream()
                .flatMap(line -> line.getLineSections().stream())
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(
                                section.getUpStation()
                                , section.getDownStation())
                        , section.getDistance()));
        return graph;
    }

    public static List<Station> findPath(List<Line> lines, Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(buildGraph(lines));
        validateGraph(source, target, dijkstraShortestPath);

        return dijkstraShortestPath.getPath(source, target).getVertexList();
    }

    public static double findPathWeight(List<Line> lines, Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(buildGraph(lines));
        validateGraph(source, target, dijkstraShortestPath);

        return dijkstraShortestPath.getPathWeight(source, target);
    }

    private static void validateGraph(Station source, Station target, DijkstraShortestPath dijkstraShortestPath) {
        if (Objects.isNull(dijkstraShortestPath.getPath(source, target))) {
            throw new DataIntegrityViolationException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
    }
}
