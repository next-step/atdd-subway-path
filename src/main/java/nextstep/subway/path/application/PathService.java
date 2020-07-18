package nextstep.subway.path.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.ShortestPathResult;

@Service
public class PathService {

    private final ShortestPathFinder shortestPathFinder;
    private final LineService lineService;

    public PathService(ShortestPathFinder shortestPathFinder, LineService lineService) {
        this.shortestPathFinder = shortestPathFinder;
        this.lineService = lineService;
    }

    public PathResponse findShortestPath(Long startId, Long endId, ShortestPathSearchType type) {
        List<LineResponse> allLines = lineService.findAllLines();
        ShortestPathResult pathResponse = Optional.ofNullable(
            shortestPathFinder.findShortestPath(allLines, startId, endId, type))
            .orElse(ShortestPathResult.empty());
        return PathResponse.of(pathResponse);
    }
}
