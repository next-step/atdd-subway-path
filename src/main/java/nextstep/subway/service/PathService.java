package nextstep.subway.service;

import nextstep.subway.controller.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nextstep.subway.controller.dto.StationResponse.stationsToStationResponses;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineRepository lineRepository;

    private StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse findPath(Long source, Long target) {
        List<Line> lines = lineRepository.findAll();
        Station sourceStation = stationService.getStationById(source);
        Station targetStation = stationService.getStationById(target);

        PathFinder pathFinder = new PathFinder();
        Path path = pathFinder.findPath(lines, sourceStation, targetStation);
        return PathResponse.builder()
                .stations(stationsToStationResponses(path.getPath()))
                .distance(path.getDistance())
                .build();

    }
}
