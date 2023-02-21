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
        try {
            return dijkstraShortestPath.getPath(source, target);
        } catch (IllegalArgumentException e) {
            //예외 번역
            throw new CustomException(CustomException.PATH_MUST_CONTAIN_STATION);
        }
    }
}
