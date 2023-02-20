package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KshortestPathFinder extends AbstractPathFinder {
    private KShortestPaths kShortestPaths;

    public KshortestPathFinder(List<Line> lines) {
        super(lines);
        kShortestPaths = new KShortestPaths(graph, 100);
    }

    public GraphPath<Long, String> getShortestPath(Long source, Long target) {
        List<GraphPath<Long, String>> paths = new ArrayList<>();
        try {
            paths = kShortestPaths.getPaths(source, target);
        }  catch (IllegalArgumentException e) {
            //예외 번역
            throw new CustomException(CustomException.PATH_MUST_CONTAIN_STATION);
        }

        GraphPath<Long, String> shortestPath =  paths.stream()
                .min(Comparator.comparingInt(path -> path.getVertexList().size()))
                .orElseThrow(() -> new CustomException(CustomException.DOES_NOT_CONNECTED_SOURCE_TO_TARGET));

        return shortestPath;
    }
}
