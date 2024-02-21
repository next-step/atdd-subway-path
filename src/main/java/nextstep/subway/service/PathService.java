package nextstep.subway.service;

import nextstep.subway.dto.path.PathResponse;
import nextstep.subway.entity.Sections;
import nextstep.subway.entity.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PathService {
    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    /** 경로 조회 */
    public PathResponse getPaths(Long source, Long target) {
        if(Objects.equals(source, target)) {
            throw new IllegalArgumentException("출발역과 도착역은 동일할 수 없다.");
        }

        Station sourceStation = stationService.findStation(source);
        Station targetStation = stationService.findStation(target);
        List<Sections> sectionsList = lineService.findSectionsList();

        PathFinder pathFinder = new PathFinder(sectionsList);
        return pathFinder.getShortestPath(sourceStation, targetStation);
    }
}
