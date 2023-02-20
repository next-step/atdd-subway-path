package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Objects;

public class DijkstraPathFinder extends AbstractPathFinder{

    private DijkstraShortestPath dijkstraShortestPath;

    public DijkstraPathFinder(List<Line> lines) {
        super(lines);
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }

    public GraphPath<Long, String> getShortestPath(Long source, Long target) {
        try {
            GraphPath<Long, String> shotestGraph = dijkstraShortestPath.getPath(source, target);

            if(Objects.isNull(shotestGraph)) {
                throw new CustomException(CustomException.DOES_NOT_CONNECTED_SOURCE_TO_TARGET);
            }

            return shotestGraph;
        } catch (IllegalArgumentException e) {
            //예외 번역
            throw new CustomException(CustomException.PATH_MUST_CONTAIN_STATION);
        }
    }
}
