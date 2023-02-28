package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineService lineService;
    private StationService stationService;
    private PathFinder pathFinder;

    public PathService(LineService lineService, StationService stationService, PathFinder pathFinder) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathFinder = pathFinder;
    }

    public PathResponse findPath(Long source, Long target) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        List<Line> lines = lineService.findAllLines();

        SubwayPath subwayPath = pathFinder.findPath(lines, sourceStation, targetStation);
        return PathResponse.of(subwayPath);
    }
}
