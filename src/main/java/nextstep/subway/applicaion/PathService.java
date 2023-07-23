package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineService lineService;
    private StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationService.findById(sourceStationId);
        Station targetStation = stationService.findById(targetStationId);
        PathFinder pathFinder = PathFinder.of(lineService.findAll());
        Path path = pathFinder.getShortestPath(sourceStation, targetStation);
        return createPathResponse(path);
    }

    private PathResponse createPathResponse(Path path) {
        return PathResponse.of(path.getStations().stream().map(stationService::createStationResponse).collect(Collectors.toList()), path.getDistance());
    }
}
