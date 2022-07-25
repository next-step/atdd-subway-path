package nextstep.subway.event;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.NonConnectionStationException;
import nextstep.subway.exception.SameStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Line> lines) {
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        addStationVertex(lines);
        addSectionsToGraph(lines);
    }

    public Path find(Station source, Station target) {
        validateDuplicateStations(source, target);
        GraphPath path = getShortestPath(source, target);
        isConnectStations(path);
        return new Path(path.getVertexList(), path.getWeight());
    }

    private GraphPath getShortestPath(Station source, Station target) {
        return new DijkstraShortestPath(graph).getPath(source, target);
    }

    private void isConnectStations(GraphPath path) {
        if (path == null) {
            throw new NonConnectionStationException();
        }
    }

    private void addStationVertex(List<Line> lines) {
        lines.forEach(line ->
                line.getStations().forEach(graph::addVertex));
    }

    private void addSectionsToGraph(List<Line> lines) {
        lines.forEach(line ->
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
