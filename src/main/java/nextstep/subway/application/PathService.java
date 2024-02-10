package nextstep.subway.application;

import nextstep.subway.application.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PathService {
    private final LineService lineService;
    private final PathFinder pathFinder;
    private final StationService stationService;

    public PathService(final LineService lineService, final PathFinder pathFinder, final StationService stationService) {
        this.lineService = lineService;
        this.pathFinder = pathFinder;
        this.stationService = stationService;
    }

    public PathResponse findPath(final Long source, final Long target) {
        final Station sourceStation = stationService.findStationById(source);
        final Station targetStation = stationService.findStationById(target);
        final List<Line> lines = lineService.findAllLine();

        return pathFinder.findPath(lines, sourceStation, targetStation);
    }
}