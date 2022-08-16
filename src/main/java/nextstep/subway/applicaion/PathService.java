package nextstep.subway.applicaion;

import java.util.List;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class PathService {
    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse findPath(Long sourceId, Long targetId) {
        if (sourceId.equals(targetId)) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }

        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);

        List<Line> lines = lineService.findLines();
        PathFinder pathFinder = new PathFinder(lines);
        return pathFinder.findPath(source, target);
    }
}
