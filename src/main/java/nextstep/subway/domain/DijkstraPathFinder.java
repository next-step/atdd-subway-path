package nextstep.subway.domain;

import nextstep.subway.exception.PathFinderException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class DijkstraPathFinder implements PathFinder {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private DijkstraShortestPath<Station, DefaultWeightedEdge> path;

    public DijkstraPathFinder(List<Line> lines) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        drawGraph(lines);
        path = new DijkstraShortestPath<>(graph);
    }

    private void drawGraph(List<Line> lines) {
        for (Line line : lines) {
            line.getStations().forEach(graph :: addVertex);
            line.getSections().forEach(section ->
                    graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance())
            );
        }
    }

    @Override
    public Path findPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new PathFinderException("출발역과 도착역은 같을 수 없습니다.");
        }

        GraphPath<Station, DefaultWeightedEdge> shortestPath;

        try {
            shortestPath = path.getPath(source, target);
        } catch (IllegalArgumentException exception) {
            throw new PathFinderException("출발역과 도착역이 연결되어 있지 않습니다.");
        }

        return new Path(shortestPath.getVertexList(), (int)shortestPath.getWeight());
    }
}
