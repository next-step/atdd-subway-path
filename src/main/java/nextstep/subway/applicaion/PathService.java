package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public PathService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(final PathRequest pathRequest) {
        List<Line> lines = lineRepository.findAll();
        PathFinder pathFinder = PathFinder.of(lines);

        Station source = stationService.findStationById(pathRequest.getSource());
        Station target = stationService.findStationById(pathRequest.getTarget());

        return pathFinder.findShortestPath(source, target);
    }
}
