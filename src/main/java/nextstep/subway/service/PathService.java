package nextstep.subway.service;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.dto.PathDto;
import org.springframework.stereotype.Service;

import nextstep.subway.exception.station.StationNotFoundException;
import java.util.List;

@Service
public class PathService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final PathFinder pathFinder;

    public PathService(StationRepository stationRepository, LineRepository lineRepository, PathFinder pathFinder) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.pathFinder = pathFinder;
    }

    public PathDto findShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationRepository.findById(sourceStationId).orElseThrow(StationNotFoundException::new);
        Station targetStation = stationRepository.findById(targetStationId).orElseThrow(StationNotFoundException::new);
        List<Line> lines = lineRepository.findAll();

        Path path = pathFinder.findShortestPathAndItsDistance(lines, sourceStation, targetStation);
        return PathDto.from(path);
    }
}
