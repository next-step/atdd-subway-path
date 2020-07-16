package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.domain.PathFindType;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.ShortestPathResult;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {
    private final PathFinder pathFinder;
    private final LineService lineService;

    public PathService(PathFinder pathFinder, LineService lineService) {
        this.pathFinder = pathFinder;
        this.lineService = lineService;
    }

    public PathResponse findShortestPath(Long startStationId, Long endStationId, PathFindType type) {
        List<LineResponse> allLines = lineService.findAllLines();

        ShortestPathResult shortestPathResult = pathFinder.findShortestPath(allLines, startStationId, endStationId, type);

        List<StationResponse> stationResponses = shortestPathResult.getStations();

        return PathResponse.with(stationResponses, shortestPathResult.getWeight());
    }
}
