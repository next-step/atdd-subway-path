package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.NotExistsStations;
import nextstep.subway.path.exception.SameStationsException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse findShortestPath(Long sourceStationId, Long targetStationId) {
        validateFindShortestPath(sourceStationId, targetStationId);

        List<Station> stations = stationService.findStations();
        List<Line> lines = lineService.findLines();
        ShortestPathFinder shortestPathFinder = new ShortestPathFinder(stations, lines);

        List<Station> shortestStations = shortestPathFinder.getShortestPath(sourceStationId, targetStationId);
        int shortestDistance = shortestPathFinder.getShortestDistance(sourceStationId, targetStationId);

        return PathResponse.of(shortestStations, shortestDistance);
    }

    private void validateFindShortestPath(Long sourceStationId, Long targetStationId) {
        if (sourceStationId.equals(targetStationId)) {
            throw new SameStationsException();
        }

        if (stationService.notExistsById(sourceStationId) || stationService.notExistsById(targetStationId)) {
            throw new NotExistsStations();
        }
    }
}
