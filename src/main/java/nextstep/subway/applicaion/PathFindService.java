package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class PathFindService {

    private final StationService stationService;
    private final LineRepository lineRepository;


    public PathFindService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    public PathResponse findShortPath(Long sourceId, Long targetId) {
        Station startStation = stationService.findById(sourceId);
        Station endStation = stationService.findById(targetId);

        List<Line> lines = lineRepository.findAll();

        PathFinder pathFinder = new DijkstraPathFinder(lines);

        return createPathResponse(pathFinder.findShortPath(startStation, endStation));
    }

    private PathResponse createPathResponse(Path path) {
        return new PathResponse(
                createStationResponse(path.getStations()),
                path.getDistance()
        );
    }



    public List<StationResponse> createStationResponse(List<Station> stations) {
        return stations.stream()
                .map(stationService::createStationResponse).collect(Collectors.toList());
    }


}
