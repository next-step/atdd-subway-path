package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.dto.PathResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
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
