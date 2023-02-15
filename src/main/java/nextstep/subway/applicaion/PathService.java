package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.section.SectionRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.vo.DijkstraPathFinder;
import nextstep.subway.vo.PathFinder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {
    private final StationService stationService;
    private final SectionService sectionService;

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        PathFinder pathFinder = new DijkstraPathFinder(stationService.findAll(), sectionService.findAll());
        Station source = stationService.findById(sourceStationId);
        Station target = stationService.findById(targetStationId);
        return pathFinder.findPath(source, target);
    }
}
