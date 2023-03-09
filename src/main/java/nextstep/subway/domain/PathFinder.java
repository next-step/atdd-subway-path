package nextstep.subway.domain;

import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final List<Station> path;
    private final int distance;

    public PathFinder(List<Line> lines, Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("시작역과 도착역은 같을 수 없습니다.");
        }

        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initGraph(lines);

        GraphPath<Station, DefaultWeightedEdge> graphPath = findPath(source, target);
        if (graphPath == null) {
            throw new IllegalArgumentException("시작역과 도착역이 이어져있지 않습니다.");
        }

        path = graphPath.getVertexList();
        distance = (int) graphPath.getWeight();
    }

    public List<Station> getPath() {
        return path;
    }

    public int getDistance() {
        return distance;
    }

    private void initGraph(List<Line> lines) {
        for (Line line : lines) {
            addVertexAndEdge(line.getSections());
        }
    }

    private void addVertexAndEdge(Sections sections) {
        for (Section section : sections.getSections()) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance());
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> findPath(Station source, Station target) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath =
            new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(source, target);
    }
}
