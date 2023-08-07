package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.constants.ErrorMessage;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.applicaion.exception.PathFinderException;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class PathService {
    private final StationService stationService;
    private final LineService lineService;

    public PathResponse findPath(Long sourceId, Long targetId) {
        if(Objects.equals(sourceId, targetId)) {
            throw new PathFinderException(ErrorMessage.SAME_BETWEEN_STATIONS);
        }

        Station sourceStation = stationService.findById(sourceId);
        Station targetStation = stationService.findById(targetId);

        PathFinder pathFinder = new PathFinder(lineService.getAllSections());
        GraphPath shortestPath = pathFinder.findPath(sourceStation, targetStation);
        return createPathFinderResponse(shortestPath);
    }

    private PathResponse createPathFinderResponse(GraphPath shortestPath) {
        List<StationResponse> list = new ArrayList<>();
        for (Station station : (List<Station>) shortestPath.getVertexList()) {
            list.add(stationService.createStationResponse(station));
        }
        return new PathResponse(list, (int)shortestPath.getWeight());
    }

}
