package nextstep.subway.applicaion;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.domain.path.PathFinderUsingWeightedMultigraph;
import nextstep.subway.domain.path.ShortestPath;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.InvalidPathSearchingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService,
        StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        ShortestPath shortestPath = findShortestPath(sourceStationId, targetStationId)
            .orElseThrow(InvalidPathSearchingException::new);

        return PathResponse.of(shortestPath.get());
    }

    @Transactional(readOnly = true)
    protected Optional<ShortestPath> findShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationService.findById(sourceStationId);
        Station targetStation = stationService.findById(targetStationId);
        List<Line> lines = lineService.findAll();

        PathFinder pathFinder = new PathFinderUsingWeightedMultigraph(lines);
        return Optional.of(pathFinder.executeDijkstra(sourceStation, targetStation));
    }
}
