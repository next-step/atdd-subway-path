package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineSectionResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationResponse;
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
        List<StationResponse> stationResponses = stationService.findAllStations();
        List<LineSectionResponse> lineSectionResponses = lineService.findAllSections();
        ShortestPathFinder shortestPathFinder = new ShortestPathFinder(stationResponses, lineSectionResponses);

        List<StationResponse> shortestStationResponses = shortestPathFinder.getShortestPath(sourceStationId, targetStationId);
        int shortestDistance = shortestPathFinder.getShortestDistance(sourceStationId, targetStationId);

        return PathResponse.of(shortestStationResponses, shortestDistance);
    }
}
