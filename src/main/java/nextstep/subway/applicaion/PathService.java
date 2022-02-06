package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long source, Long target) {
        List<Line> totalLines = lineRepository.findAll();
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        Path path = Path.findShortestPath(totalLines, sourceStation, targetStation);
        return PathResponse.of(path.getStations(), path.getDistance());
    }

}
