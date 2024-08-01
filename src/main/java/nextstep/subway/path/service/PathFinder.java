package nextstep.subway.path.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.service.LineService;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PathFinder {

    private StationService stationService;
    private LineService lineService;
    private PathService pathService;

    public PathFinder(StationService stationService, LineService lineService, PathService pathService) {
        this.stationService = stationService;
        this.lineService = lineService;
        this.pathService = pathService;
    }

    @Transactional(readOnly = true)
    public PathResponse retrieveStationPath(Long source, Long target) {
        validateStationExist(source, target);
        List<Line> lineList = lineService.getAllLines();
        return pathService.findPath(source, target, lineList);
    }

    private void validateStationExist(Long source, Long target) {
        stationService.getStationByIdOrThrow(source);
        stationService.getStationByIdOrThrow(target);
    }

}
