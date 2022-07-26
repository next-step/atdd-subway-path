package nextstep.subway.domain;

import nextstep.subway.exception.FindPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.*;

public class PathFinder {

    private WeightedGraph<Station, DefaultWeightedEdge> graph;
    private ShortestPathAlgorithm<Station, DefaultWeightedEdge> pathAlgorithm;

    private PathFinder(List<Line> lines) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        this.pathAlgorithm = new DijkstraShortestPath<>(this.graph);
        initGraph(lines);
    }

    public static PathFinder of(List<Line> lines) {
        return new PathFinder(lines);
    }

    private void initGraph(List<Line> lines) {
        addVertexToGraph(lines);
        addEdgeToGraph(lines);
    }

    private void addVertexToGraph(List<Line> lines) {
        lines.stream()
                .map(Line::allStations)
                .flatMap(Collection::stream)
                .distinct()
                .forEach(graph::addVertex);
    }

    private void addEdgeToGraph(List<Line> lines) {
        lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    public List<Station> findPath(Station source, Station target) {
        GraphPath<Station, DefaultWeightedEdge> path = pathAlgorithm.getPath(source, target);
        notConnectedSourceAndTarget(path);
        sameSourceAndTarget(path);

        return unmodifiableList(path.getVertexList());
    }

    private void sameSourceAndTarget(GraphPath<Station, DefaultWeightedEdge> path) {
        if (path.getStartVertex().equals(path.getEndVertex())) {
            throw new FindPathException("출발역과 도착역이 같을 수는 없습니다.");
        }
    }

    private void notConnectedSourceAndTarget(GraphPath<Station, DefaultWeightedEdge> path) {
        if (path == null) {
            throw new FindPathException("출발역과 도착역이 연결이 되어 있지 않습니다.");
        }
    }

    public double findPathWeight(Station source, Station target) {
        return pathAlgorithm.getPath(source, target).getWeight();
    }
}