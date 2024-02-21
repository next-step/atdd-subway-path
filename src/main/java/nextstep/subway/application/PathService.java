package nextstep.subway.application;

import nextstep.subway.application.dto.PathResult;
import nextstep.subway.dto.PathRequest;
import nextstep.subway.dto.PathResponse;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private final LineService lineService;

    private final PathFinder pathFinder;

    public PathService(LineService lineService, PathFinder pathFinder) {
        this.lineService = lineService;
        this.pathFinder = pathFinder;
    }

    public PathResponse findShortestPath(PathRequest pathRequest) {
        validatePathRequest(pathRequest);

        return convertPathResponse(pathFinder.calculateShortestPath(
                lineService.findAllLines(),
                lineService.findStation(pathRequest.getDepartureStationId()),
                lineService.findStation(pathRequest.getArrivalStationId())));
    }

    private void validatePathRequest(PathRequest pathRequest) {
        if (areStationsSame(pathRequest)) {
            throw new IllegalArgumentException("출발역과 도착역이 동일할 수 없습니다.");
        }
    }

    private boolean areStationsSame(PathRequest pathRequest) {
        return pathRequest.getDepartureStationId().equals(pathRequest.getArrivalStationId());
    }

    private PathResponse convertPathResponse(PathResult pathResult) {
        return new PathResponse(pathResult.getStations(), pathResult.getDistance());
    }
}
