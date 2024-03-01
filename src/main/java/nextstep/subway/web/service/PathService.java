package nextstep.subway.web.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Path;
import nextstep.subway.domain.entity.PathFinder;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.web.dto.response.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        Station source = stationService.getStationById(sourceStationId);
        Station target = stationService.getStationById(targetStationId);
        List<Line> lines = lineService.getLines();
        PathFinder pathFinder = new PathFinder(lines);
        Path shortestPath = pathFinder.findShortestPath(source, target);
        return new PathResponse(shortestPath);
    }


}
