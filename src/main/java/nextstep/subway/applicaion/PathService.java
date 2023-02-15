package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineService lineService;
    private final StationService stationService;
    private final ShortestPathFinder shortestPathFinder;

    public PathService(final LineService lineService, final StationService stationService, final ShortestPathFinder shortestPathFinder) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.shortestPathFinder = shortestPathFinder;
    }

    public PathResponse showRoutes(final Long source, final Long target) {

        final Station sourceStation = stationService.findById(source);
        final Station targetStation = stationService.findById(target);
        final List<Line> lines = lineService.findAllLines();
        final PathResult pathResult = shortestPathFinder.findRoute(Path.of(lines, sourceStation, targetStation));
        return PathResponse.from(pathResult);
    }
}
