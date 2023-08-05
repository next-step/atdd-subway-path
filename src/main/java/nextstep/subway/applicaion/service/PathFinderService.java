package nextstep.subway.applicaion.service;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.dto.path.PathFinderResponse;
import nextstep.subway.applicaion.dto.station.StationResponse;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.domain.station.Station;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        List<Section> allSections = lineService.findAllSections();
        PathFinder pathFinder = new PathFinder(allSections);

        Station sourceStation = stationService.findById(sourceId);
        Station targetStation = stationService.findById(targetId);
        GraphPath shortestPath = pathFinder.findPath(sourceStation, targetStation);
        return createPathFinderResponse(shortestPath);
    }

    private PathFinderResponse createPathFinderResponse(GraphPath shortestPath) {
        List<StationResponse> stationResponseList = new ArrayList<>();
        for (Station station : (List<Station>) shortestPath.getVertexList()) {
            stationResponseList.add(stationService.createStationResponse(station));
        }
        return new PathFinderResponse(stationResponseList, (int)shortestPath.getWeight());
    }
}