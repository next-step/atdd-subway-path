package nextstep.subway.path.application;

import nextstep.subway.path.dto.ShortestPathResponse;
import nextstep.subway.station.domain.StationRepository;

public class PathService {

    private final ShortestPathFinder shortestPathFinder;
    private final StationRepository stationRepository;

    public PathService(ShortestPathFinder shortestPathFinder,
        StationRepository stationRepository) {
        this.shortestPathFinder = shortestPathFinder;
        this.stationRepository = stationRepository;
    }

    public ShortestPathResponse findShortestPath(Long startId, Long endId) {
        shortestPathFinder.findShortestDistance(null, null);
        return new ShortestPathResponse(startId, endId);
    }
}
