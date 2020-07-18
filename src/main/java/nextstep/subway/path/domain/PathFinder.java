package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;

import java.util.List;

public abstract class PathFinder {
    private static final ShortestPathFinder SHORTEST_PATH_FINDER = new ShortestPathFinder();
    private static final FastestPathFinder FASTEST_PATH_FINDER = new FastestPathFinder();

    public static PathFinder getPathFinder(PathType pathType) {
        switch (pathType) {
            case DISTANCE:
                return SHORTEST_PATH_FINDER;
            case DURATION:
                return FASTEST_PATH_FINDER;
            default:
                throw new IllegalArgumentException("Invalid PathType: " + pathType);
        }
    }

    public abstract List<LineStation> findPath(List<Line> lines, Long sourceStationId, Long targetStationId);
}
