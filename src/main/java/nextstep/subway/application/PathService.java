package nextstep.subway.application;

import nextstep.subway.application.dto.PathResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PathService {

    private StationRepository stationRepository;
    private LineRepository lineRepository;

    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional(readOnly = true)
    public PathResponse getPath(Long source, Long target) {
        Station sourceStation = stationRepository.findById(source).orElseThrow(IllegalArgumentException::new);
        Station targetStation = stationRepository.findById(target).orElseThrow(IllegalArgumentException::new);
        List<Line> lines = lineRepository.findAll();

        PathFinder pathFinder = new PathFinder(lines);
        return PathResponse.from(
                pathFinder.getShortestPathStations(sourceStation, targetStation),
                pathFinder.getShortestPathDistance(sourceStation, targetStation)
        );
    }
}
