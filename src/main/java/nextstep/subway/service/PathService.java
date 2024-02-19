package nextstep.subway.service;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.service.dto.LineDto;
import nextstep.subway.service.dto.PathDto;
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

    public PathDto findShortestPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = stationService.findStationById(sourceStationId);
        Station targetStation = stationService.findStationById(targetStationId);
        List<Line> lines = lineService.findAllLines()
                .stream()
                .map(LineDto::toEntity)
                .collect(Collectors.toList());

        Path path = pathFinder.findShortestPathAndItsDistance(lines, sourceStation, targetStation);
        return PathDto.from(path);
    }
}
