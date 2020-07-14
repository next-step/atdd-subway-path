package nextstep.subway.path.application;

import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.stereotype.Service;
import nextstep.subway.station.domain.StationRepository;

@Service
public class PathService {
    private final PathFinder pathFinder;
    private final StationRepository stationRepository;

    public PathService(PathFinder pathFinder, StationRepository stationRepository) {
        this.pathFinder = pathFinder;
        this.stationRepository = stationRepository;
    }

    public PathResponse findShortestPath(long startStationId, long endStationId) {
        return null;
    }
}
