package nextstep.subway.domain;

import nextstep.subway.exception.NonConnectionStationException;
import nextstep.subway.exception.SameStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {
    private List<Line> lines;
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        addStationVertex();
        addSectionsToGraph();
    }

    public Path find(Station source, Station target) {
        validateDuplicateStations(source, target);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath path = dijkstraShortestPath.getPath(source, target);

        isConnectStations(path);
        return new Path(path.getVertexList(), path.getWeight());
    }

    private void isConnectStations(GraphPath path) {
        if (path == null) {
            throw new NonConnectionStationException();
        }
    }

    private void addStationVertex() {
        this.lines.forEach(line ->
                line.getStations().forEach(graph::addVertex));
    }

    private void addSectionsToGraph() {
        this.lines.forEach(line ->
            line.getSections().forEach(section ->
                    graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()))
        );
    }

    private void validateDuplicateStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new SameStationException();
        }
    }
}
