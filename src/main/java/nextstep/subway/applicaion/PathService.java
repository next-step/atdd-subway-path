package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PathService {
    private final LineService lineService;
    private final StationService stationService;
    private final JGraphPathFinder pathFinder;

    public PathService(LineService lineService, StationService stationService, JGraphPathFinder pathFinder) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathFinder = pathFinder;
    }

    @Transactional
    public PathResponse getShortestPath(Long source, Long target) {
        List<Line> lines = lineService.findAll();
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        return pathFinder.find(lines, sourceStation, targetStation);
    }
}
