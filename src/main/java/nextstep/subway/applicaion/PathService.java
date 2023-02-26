package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.ShortestPathFinder;
import nextstep.subway.domain.SubwayMap;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private final LineService lineService;
    private final StationService stationService;
    private final ShortestPathFinder shortestPathFinder;

    public PathService(
        LineService lineService,
        StationService stationService,
        ShortestPathFinder shortestPathFinder
    ) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.shortestPathFinder = shortestPathFinder;
    }

    public PathResponse findPath(Long source, Long target) {
        final List<Line> lines = lineService.findAllLine();

        final SubwayMap subwayMap = new SubwayMap(lines);
        final Path path = subwayMap.findPath(source, target, shortestPathFinder);

        final List<StationResponse> stationResponses = path.getStationIds().stream()
            .map(it -> stationService.findStationById(it))
            .collect(Collectors.toList());

        return new PathResponse(stationResponses, path.getDistance());
    }
}
