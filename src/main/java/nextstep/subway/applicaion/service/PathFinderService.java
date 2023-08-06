package nextstep.subway.applicaion.service;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.dto.path.PathFinderResponse;
import nextstep.subway.applicaion.dto.station.StationResponse;
import nextstep.subway.applicaion.exception.domain.PathFinderException;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.domain.station.Station;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class PathFinderService {

    private final StationService stationService;
    private final LineService lineService;

    public PathFinderService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathFinderResponse findPath(Long sourceId, Long targetId) {
        PathFinder pathFinder = new PathFinder(lineService.findAllSections());
        Station sourceStation = stationService.findById(sourceId);
        Station targetStation = stationService.findById(targetId);

        checkFindPathValidity(sourceStation, targetStation);
        GraphPath shortestPath = pathFinder.findPath(sourceStation, targetStation);
        return createPathFinderResponse(shortestPath);
    }

    private void checkFindPathValidity(Station sourceStation, Station targetStation) {
        if (sourceStation.getId().equals(targetStation.getId())) {
            throw new PathFinderException("출발역과 도착역이 동일합니다.");
        }
    }

    private PathFinderResponse createPathFinderResponse(GraphPath shortestPath) {
        List<StationResponse> stationResponseList = new ArrayList<>();
        for (Station station : (List<Station>) shortestPath.getVertexList()) {
            stationResponseList.add(stationService.createStationResponse(station));
        }
        return new PathFinderResponse(stationResponseList, (int)shortestPath.getWeight());
    }
}