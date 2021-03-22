package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.domain.DijkstraStrategy;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PathFinderService {

    private final StationService stationService;
    private final LineService lineService;

    public PathFinderService(StationService stationService, LineService lineService){
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional(readOnly = true)
    public Stations findShortestPath(Long source, Long path) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(path);

        List<Line> lines = lineService.findLines();

        PathFinder pathFinder = new PathFinder(new DijkstraStrategy(lines));

        return pathFinder.findShortestPath(sourceStation, targetStation);
    }
}
