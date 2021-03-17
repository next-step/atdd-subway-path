package nextstep.subway.paths.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.paths.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PathService {
    private LineService lineService;
    private StationService stationService;
    private PathFinder pathFinder;

    public PathService(LineService lineService, StationService stationService, PathFinder pathFinder) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathFinder = pathFinder;
    }

    public PathResponse searchShortestPath(Long sourceId, Long targetId) {
        final Station source = stationService.findStationById(sourceId);
        final Station target = stationService.findStationById(targetId);
        return searchPath(source, target);
    }

    private PathResponse searchPath(Station source, Station target) {
        pathFinder.initialize(lineService.findAllLines());
        return pathFinder.searchShortestPath(source, target);
    }
}
