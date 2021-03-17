package nextstep.subway.path.domain;

import nextstep.subway.path.exception.SourceEqualsWithTargetException;
import nextstep.subway.path.exception.StationNotExistsException;
import nextstep.subway.path.exception.StationsNotConnectedException;
import nextstep.subway.station.domain.Station;
import org.apache.commons.lang3.ObjectUtils;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class PathFinder {
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public GraphPath getShortestPath(Station source, Station target) {
        Optional<GraphPath> path;

        if(source.equals(target)) {
            throw new SourceEqualsWithTargetException();
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        try {
            path = Optional.ofNullable(dijkstraShortestPath.getPath(source, target));
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new StationNotExistsException();
        }

        return path.orElseThrow(StationsNotConnectedException::new);
    }
}
