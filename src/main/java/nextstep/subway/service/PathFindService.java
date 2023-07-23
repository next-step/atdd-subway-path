package nextstep.subway.service;

import nextstep.subway.common.NotFoundStationException;
import nextstep.subway.controller.resonse.PathResponse;
import nextstep.subway.controller.resonse.StationResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.vo.Path;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PathFindService {

    private final PathFinder pathFinder;
    private final StationRepository stationRepository;

    public PathFindService(PathFinder pathFinder, StationRepository stationRepository) {
        this.pathFinder = pathFinder;
        this.stationRepository = stationRepository;
    }

    public PathResponse getShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationRepository.findById(sourceStationId)
                .orElseThrow(() -> new NotFoundStationException(sourceStationId));
        Station targetStation = stationRepository.findById(targetStationId)
                .orElseThrow(() -> new NotFoundStationException(targetStationId));

        Path shortestPath = pathFinder.getShortestPath(sourceStation, targetStation);
        return new PathResponse(
                shortestPath.getStations().stream().map(StationResponse::new).collect(Collectors.toList()),
                shortestPath.getDistance()
        );

    }
}
