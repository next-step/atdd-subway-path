package nextstep.subway.path;

import nextstep.subway.exception.SameSourceAndTargetException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PathService {

    private final LineService lineService;
    private final StationService stationService;
    private final PathFinder pathFinder;

    public PathService(LineService lineService, StationService stationService, PathFinder pathFinder) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathFinder = pathFinder;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        if(sourceId == targetId) {
            throw new SameSourceAndTargetException();
        }
        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);
        List<Section> sections = lineService.getSections();

        return PathResponse.of(pathFinder.findPath(sections, source, target));
    }
}
