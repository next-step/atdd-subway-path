package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    private final StationService stationService;
    private final LineService lineService;
    private final PathFinder pathFinder;

    public PathService(StationService stationService, LineService lineService, PathFinder pathFinder) {
        this.stationService = stationService;
        this.lineService = lineService;
        this.pathFinder = pathFinder;
    }

    public PathResponse findPath(PathRequest pathRequest) {
        if (pathRequest.isSame()) {
            throw new IllegalArgumentException("출발지와 도착지가 동일할 수 없습니다.");
        }
        Station source = stationService.findById(pathRequest.getSource());
        Station target = stationService.findById(pathRequest.getTarget());
        List<Line> lines = lineService.findAll();
        Path path = pathFinder.find(lines, source, target);
        return PathResponse.toResponse(
                path.getDistance(),
                path.getRoutes()
                        .stream()
                        .map(stationService::findById)
                        .collect(Collectors.toList()));
    }
}
