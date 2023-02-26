package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.global.error.exception.ErrorCode;
import nextstep.subway.global.error.exception.InvalidValueException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class ShortPathFinder {

    public static PathResponse findShortPath(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        int distance = (int) dijkstraShortestPath.getPathWeight(source, target);
        List<Station> paths = checkConnectedStationsAndGetPaths(source, target, dijkstraShortestPath);

        return getPathResponse(distance, paths);
    }

    private static List checkConnectedStationsAndGetPaths(Station source, Station target, DijkstraShortestPath dijkstraShortestPath) {
        return Optional.ofNullable(dijkstraShortestPath.getPath(source, target))
                .map(graphPath -> graphPath.getVertexList())
                .orElseThrow(() -> {
                    throw new InvalidValueException(ErrorCode.STATIONS_NOT_CONNECTED);
                });
    }

    private static PathResponse getPathResponse(int distance, List<Station> paths) {
        return PathResponse.builder()
                .stations(paths)
                .distance(distance)
                .build();
    }
}
