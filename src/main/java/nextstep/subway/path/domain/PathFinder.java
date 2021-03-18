package nextstep.subway.path.domain;

import nextstep.subway.path.exception.SourceEqualsWithTargetException;
import nextstep.subway.path.exception.StationNotExistsException;
import nextstep.subway.path.exception.StationsNotConnectedException;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Optional;

public class PathFinder {
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public GraphPath getShortestPath(Station source, Station target) {
        if(source.equals(target)) {
            throw new SourceEqualsWithTargetException();
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        Optional<GraphPath> path = getDijkstarPathOrThrowException(source, target, dijkstraShortestPath);

        return path.orElseThrow(StationsNotConnectedException::new);
    }

    private Optional<GraphPath> getDijkstarPathOrThrowException(Station source, Station target, DijkstraShortestPath dijkstraShortestPath) {
        Optional<GraphPath> path;
        try {
            path = Optional.ofNullable(dijkstraShortestPath.getPath(source, target));
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new StationNotExistsException();
        }
        return path;
    }
}
