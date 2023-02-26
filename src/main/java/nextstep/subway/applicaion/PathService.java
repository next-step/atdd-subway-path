package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import nextstep.subway.infra.DijkstraPathFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineRepository lineRepository;
    private StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationService.findById(sourceStationId);
        Station targetStation = stationService.findById(targetStationId);

        List<Line> allLines = lineRepository.findAll();
        PathFinder pathFinder = DijkstraPathFinder.create(allLines);
        Path path = pathFinder.findShortestPath(sourceStation, targetStation);

        return PathResponse.from(path);
    }
}
