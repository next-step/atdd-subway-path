package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse searchShortestPath(Long sourceStationId, Long targetStationId) {
        List<Line> lines = lineRepository.findAll();
        PathFinder pathFinder = new PathFinder(lines);

        List<LineStation> shortestPathLineStations = pathFinder.searchShortestPath(sourceStationId, targetStationId);

        List<Long> shortestPathStationIds = shortestPathLineStations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());

        List<Station> shortestPathStations = stationRepository.findByIdIn(shortestPathStationIds);
        return new PathResponse(shortestPathLineStations, shortestPathStations);
    }
}
