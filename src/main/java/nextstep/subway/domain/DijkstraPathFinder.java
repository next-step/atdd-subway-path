package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Objects;

public class DijkstraPathFinder implements PathFinder{

    private DijkstraShortestPath dijkstraShortestPath;

    public DijkstraPathFinder(List<Line> lines) {
        lines.forEach(this::registLine);
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    @Override
    public GraphPath<Long, String> getPath(Long source, Long target) {
        GraphPath<Long, String> path = dijkstraShortestPath.getPath(source, target);

        if(Objects.isNull(path)) {
            throw new CustomException(CustomException.DOES_NOT_CONNECTED_SOURCE_TO_TARGET);
        }

        return path;
    }
}
