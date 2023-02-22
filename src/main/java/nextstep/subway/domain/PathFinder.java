package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public PathFinder(final List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        drawGraph(lines, graph);
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    private void drawGraph(final List<Line> lines, final WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        for (Line line : lines) {
            line.getAllStations().forEach(graph::addVertex);
            line.getSections().forEach(section -> graph.setEdgeWeight(
                    graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance())
            );
        }
    }

    /**
     * 다익스트라 알고리즘을 사용하여 최단 경로 정보를 조회합니다.
     *
     * @param source 출발지 역
     * @param target 도착지 역
     * @return 최단 경로 정보 반환
     */
    public ShortestPath findShortestPathWithDijkstra(final Station source, final Station target) {
        return ShortestPath.from(dijkstraShortestPath.getPath(source, target));
    }
}
