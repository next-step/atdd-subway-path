package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathService {

    private StationRepository stationRepository;

    private LineRepository lineRepository;

    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(Long sourceId, Long targetId) {

        Station source = stationRepository.findById(sourceId).orElseThrow(IllegalArgumentException::new);
        Station target = stationRepository.findById(targetId).orElseThrow(IllegalArgumentException::new);

        PathFinder pathFinder = PathFinder.of(lineRepository.findAll());
        pathFinder.paths(source, target);

        return pathFinder.paths(source, target);
    }

}
