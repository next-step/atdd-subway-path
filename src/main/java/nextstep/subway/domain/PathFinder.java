package nextstep.subway.domain;

import nextstep.subway.exception.SameStationException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {
    private Station source;
    private Station target;

    public PathFinder(Station source, Station target) {
        validateDuplicateStations(source, target);

        this.source = source;
        this.target = target;
    }

    public Path find(Line line) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = line.toGraph();
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> pathInStations = dijkstraShortestPath.getPath(source, target).getVertexList();
        int distance = (int) dijkstraShortestPath.getPathWeight(source, target);
        return new Path(pathInStations, distance);
    }

    private void validateDuplicateStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new SameStationException();
        }
    }
}
