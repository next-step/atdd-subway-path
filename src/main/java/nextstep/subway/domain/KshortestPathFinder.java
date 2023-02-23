package nextstep.subway.domain;

import nextstep.subway.exception.CustomException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KshortestPathFinder implements PathFinder {
    private KShortestPaths kShortestPaths;

    public KshortestPathFinder(List<Line> lines) {
        lines.forEach(this::registLine);
        kShortestPaths = new KShortestPaths(graph, 100);
    }

    @Override
    public GraphPath<Long, String> getPath(Long source, Long target) {
        List<GraphPath<Long, String>> paths = kShortestPaths.getPaths(source, target);

        GraphPath<Long, String> shortestPath =  paths.stream()
                .min(Comparator.comparingInt(path -> path.getVertexList().size()))
                .orElseThrow(() -> new CustomException(CustomException.DOES_NOT_CONNECTED_SOURCE_TO_TARGET));

        return shortestPath;
    }
}
