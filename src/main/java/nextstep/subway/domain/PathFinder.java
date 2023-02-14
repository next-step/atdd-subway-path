package nextstep.subway.domain;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.exception.IdenticalSourceTargetNotAllowedException;
import nextstep.subway.exception.NonConnectedSourceTargetException;
import nextstep.subway.exception.StationNotFoundException;

public class PathFinder {

    private final List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
    }

    public Path findPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new IdenticalSourceTargetNotAllowedException(source.getName(), target.getName());
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for (Line line : lines) {
            line.getStations().forEach(graph::addVertex);
            line.getSections().forEach(section -> {
                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
                graph.setEdgeWeight(edge, section.getDistance());
            });
        }

        if (!graph.containsVertex(source)) {
            throw new StationNotFoundException(source.getId());
        }

        if (!graph.containsVertex(target)) {
            throw new StationNotFoundException(target.getId());
        }

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> result = dijkstraShortestPath.getPath(source, target);

        if (result == null) {
            throw new NonConnectedSourceTargetException(source.getName(), target.getName());
        }

        return new Path(result.getVertexList(), (int) result.getWeight());
    }
}
