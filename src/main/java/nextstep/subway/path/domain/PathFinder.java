package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<LineStation> findPath(List<Line> lines, Long sourceStationId, Long targetStationId) {
        Map<Long, LineStation> lineStations = this.getLineStations(lines);
        this.checkStationsAreExisting(lineStations, sourceStationId, targetStationId);

        List<Long> pathStationIds = this.getPath(lineStations, sourceStationId, targetStationId);
        return this.toLineStations(lineStations, pathStationIds);
    }

    private Map<Long, LineStation> getLineStations(List<Line> lines) {
        return lines.stream()
                .flatMap(it -> it.getLineStations().getLineStations().stream())
                .collect(Collectors.toMap(it -> it.getStationId(), it -> it, (it1, it2) -> it1));
    }

    private void checkStationsAreExisting(Map<Long, LineStation> lineStations, Long sourceStationId, Long targetStationId) {
        if (!lineStations.containsKey(sourceStationId) || !lineStations.containsKey(targetStationId)) {
            throw new CannotFindPathException();
        }
    }

    protected abstract List<Long> getPath(Map<Long, LineStation> lineStations, Long sourceStationId, Long targetStationId);

    private List<LineStation> toLineStations(Map<Long, LineStation> lineStations, List<Long> shortestPathStationIds) {
        return shortestPathStationIds.stream()
                .map(lineStations::get)
                .collect(Collectors.toList());
    }
}
