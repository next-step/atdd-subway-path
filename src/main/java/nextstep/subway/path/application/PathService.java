package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import org.springframework.stereotype.Service;

@Service
public class PathService {
    private final LineService lineService;
    private final StationService stationService;
    private final PathFinder pathFinder;

    public PathService(LineService lineService, StationService stationService, PathFinder pathFinder) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathFinder = pathFinder;
    }

    public PathResponse getPath(long sourceId, long targetId) {

        validateRequestStations(sourceId, targetId);

        return PathResponse.of(
                pathFinder.getShortestPath(
                        lineService.getAllSections(),
                        stationService.findById(sourceId),
                        stationService.findById(targetId)
                )
        );
    }

    private void validateRequestStations(long sourceId, long targetId) {
        if (sourceId == targetId) {
            throw new RuntimeException("출발역과 도착역이 같습니다.");
        }
    }

}
