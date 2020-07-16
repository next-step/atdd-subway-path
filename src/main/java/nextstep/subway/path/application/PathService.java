package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.domain.PathFindType;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.ShortestPathResult;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    private final PathFinder pathFinder;
    private final LineService lineService;

    public PathService(PathFinder pathFinder, LineService lineService) {
        this.pathFinder = pathFinder;
        this.lineService = lineService;
    }

    public PathResponse findShortestPath(Long startStationId, Long endStationId, PathFindType type) {
        List<LineResponse> allLine = lineService.findAllLines();

        ShortestPathResult shortestPathResult = pathFinder.findShortestPath(allLine, startStationId, endStationId, type);

        List<StationResponse> stationResponses = shortestPathResult.getStations();

        List<LineStationResponse> lineStationResponses = shortestPathResult.toLineStationResponse(allLine);

        return PathResponse.with(stationResponses, shortestPathResult.getWeight(),
                lineStationResponses.stream().mapToInt(LineStationResponse::getDuration).sum(),
                lineStationResponses.stream().mapToInt(LineStationResponse::getDistance).sum());
    }
}
