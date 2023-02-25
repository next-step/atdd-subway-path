package nextstep.subway.domain;

import java.util.List;

public class SubwayMap {

    private final List<Line> lines;

    public SubwayMap(List<Line> lines) {
        this.lines = lines;
    }

    public Path findPath(Long source, Long target, ShortestPathFinder shortestPathFinder) {
        if (source.equals(target)) {
            throw new IllegalArgumentException();
        }
        return shortestPathFinder.findShortestPath(lines, source, target);
    }
}
