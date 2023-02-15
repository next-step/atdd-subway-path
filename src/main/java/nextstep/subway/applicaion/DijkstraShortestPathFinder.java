package nextstep.subway.applicaion;

import nextstep.subway.common.exception.NoPathConnectedException;
import nextstep.subway.domain.*;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

import static nextstep.subway.common.error.SubwayError.NO_PATH_CONNECTED;

@Component
public class DijkstraShortestPathFinder implements ShortestPathFinder {

    final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    final DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

    @Override
    public PathResult findRoute(final Path path) {
        addVertex(path.getStations());
        addEdge(path.getSections());
        final GraphPath graphPath = getGraphPath(path);
        validateNotConnected(graphPath);
        final double pathWeight = getPathWeight(path);
        return PathResult.of(graphPath.getVertexList(), pathWeight);
    }

    private double getPathWeight(final Path path) {
        return dijkstraShortestPath.getPathWeight(path.getSourceStation(), path.getTargetStation());
    }

    private GraphPath getGraphPath(final Path path) {
        return dijkstraShortestPath.getPath(path.getSourceStation(), path.getTargetStation());
    }

    private void validateNotConnected(final GraphPath graphPath) {
        if (graphPath == null) {
            throw new NoPathConnectedException(NO_PATH_CONNECTED);
        }
    }

    private void addVertex(final List<Station> stations) {
        stations.stream()
                .distinct()
                .forEach(graph::addVertex);
    }

    private void addEdge(final List<Section> sections) {
        sections.stream()
                .forEach(this::addEdge);
    }

    private void addEdge(final Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance().getDistance());
    }
}
