package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.exception.PathNotFoundException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathResult getShortestPath(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new PathNotFoundException(sourceStation.getId(), targetStation.getId());
        }

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> shortestPathStationGraph = dijkstraAlg.getPath(sourceStation, targetStation);

        if (shortestPathStationGraph == null) {
            throw new PathNotFoundException(sourceStation.getId(), targetStation.getId());
        }

        List<Station> path = shortestPathStationGraph.getVertexList();
        int totalDistance = (int) shortestPathStationGraph.getWeight();

        return new PathResult(path, totalDistance);
    }
}
