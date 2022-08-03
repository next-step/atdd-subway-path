package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.ShortestRoute;
import nextstep.subway.domain.Station;
import nextstep.subway.infrastructure.PathFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PathService {

    private final LineService lineService;

    private final StationService stationService;

    public PathService(final LineService lineService, final StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Transactional(readOnly = true)
    public ShortestRoute findRoutes(Long source, Long target) {

        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        List<Line> lines = lineService.findAll();
        PathFinder pathFinder = new PathFinder(lines);
        return pathFinder.findRoutes(sourceStation, targetStation);
    }
}
