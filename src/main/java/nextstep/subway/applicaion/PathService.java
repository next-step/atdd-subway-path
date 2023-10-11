package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineRepository lineRepository;
    private StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse showPath(PathRequest request) {
        PathFinder pathFinder = new PathFinder(lineRepository.findAll());
        Station source = stationService.findById(request.getSource());
        Station target = stationService.findById(request.getTarget());
        pathFinder.calculatePath(source, target);
        return PathResponse.from(pathFinder);
    }
}
