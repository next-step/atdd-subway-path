package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

public class PathFinderService {
    private final LineService lineService;
    private final StationService stationService;
    private final PathFinder pathFinder;

    public PathFinderService(LineService lineService, StationService stationService, PathFinder pathFinder) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathFinder = pathFinder;
    }

    public PathResponse findPath(Long sourceId, Long targetId) {
        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);
        List<Line> lines = lineService.findAll();

        pathFinder.init(lines);

        return pathFinder.find(source, target);
    }
}
