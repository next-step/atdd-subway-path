package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayMap;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.FindPathException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {
    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public Path find(Long sourceId, Long targetId) {
        if (sourceId.equals(targetId)) {
            throw new FindPathException(ErrorType.SAME_SOURCE_AND_TARGET);
        }
        try {
            Station source = stationService.findById(sourceId);
            Station target = stationService.findById(targetId);
            List<Line> lines = lineService.findAllLines();
            SubwayMap subwayMap = SubwayMap.create(lines);
            return subwayMap.findShortestPath(source, target);
        } catch (IllegalArgumentException e) {
            throw new FindPathException(ErrorType.NOT_EXIST_SOURCE_AND_TARGET);
        }
    }
}
