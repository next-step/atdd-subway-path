package nextstep.subway.applicaion;

import java.util.List;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public PathService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse findPathBy(final long source, final long target) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);
        List<Line> lines = lineRepository.findAll();

        PathFinder pathFinder = new PathFinder(lines);
        return createPathResponse(pathFinder.find(sourceStation, targetStation));
    }

    private PathResponse createPathResponse(final GraphPath graphPath) {
        List<StationResponse> stations = stationService.createStationResponsesBy(graphPath.getVertexList());
        double distance = graphPath.getWeight();
        return new PathResponse(stations, distance);
    }
}
