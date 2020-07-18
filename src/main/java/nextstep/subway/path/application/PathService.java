package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathFinderResult;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.ui.FindType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

    private final LineService lineService;

    public PathService(LineService lineService) {
        this.lineService = lineService;
    }

    public PathResponse findShortestPath(long srcStationId, long dstStationId, FindType type) {
        final List<LineResponse> lines = lineService.findAllLines();
        final PathFinder pathFinder = new PathFinder();
        final PathFinderResult result = pathFinder.findPath(lines, srcStationId, dstStationId, type);
        return result.toPathResponse(lines);
    }
}
