package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private StationService stationService;
    private LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse getShortestPathListAndDistance(Long sourceId, Long targetId) {
        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);
        List<Line> lines = lineService.findLines();
        PathFinder pathFinder = new PathFinder(lines);
        return new PathResponse(pathFinder.getShortestPathList(source, target).stream()
                .map(StationResponse::of)
                .collect(Collectors.toList()),
                pathFinder.getShortestDistance(source, target));
    }

}
