package nextstep.subway.applicaion;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse findPath(Long source, Long target) {
        Station departure = stationService.findById(source);
        Station destination = stationService.findById(target);
        List<Line> lines = lineService.findAll();

        PathFinder pathFinder = new PathFinder(lines);
        Path path = pathFinder.findPath(departure, destination);
        return new PathResponse(path);
    }
}
