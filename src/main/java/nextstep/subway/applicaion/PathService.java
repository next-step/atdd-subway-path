package nextstep.subway.applicaion;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.domain.path.PathFinderUsingWeightedMultigraph;
import nextstep.subway.domain.path.ShortestPath;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PathService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    @Autowired
    public PathService(LineRepository lineRepository,
        StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        ShortestPath shortestPath = findShortestPath(sourceStationId, targetStationId);

        return PathResponse.of(shortestPath.get());
    }

    @Transactional(readOnly = true)
    protected ShortestPath findShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationRepository.findById(sourceStationId)
            .orElseThrow(NoSuchElementException::new);
        Station targetStation = stationRepository.findById(targetStationId)
            .orElseThrow(NoSuchElementException::new);
        List<Line> lines = lineRepository.findAll();

        PathFinder pathFinder = new PathFinderUsingWeightedMultigraph(lines);
        return pathFinder.executeDijkstra(sourceStation, targetStation);
    }
}
