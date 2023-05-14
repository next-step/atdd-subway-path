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
public class PathService {

    private final StationService stationService;
    private final LineRepository lineRepository;

    public PathService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    public PathResponse findPath(Long source, Long target) {
        Station start = stationService.findById(source);
        Station end = stationService.findById(target);

        List<Line> lines = lineRepository.findAll();
        PathFinder pathFinder = new DijkstraPathFinder(lines);

        return createPathResponse(pathFinder.findPath(start, end));
    }

    private PathResponse createPathResponse(Path path) {
        return new PathResponse(createStationResponse(path.getStations()), path.getDistance());
    }

    private List<StationResponse> createStationResponse(List<Station> stations) {
        return stations.stream()
                .map(stationService::createStationResponse).collect(Collectors.toList());
    }

}
