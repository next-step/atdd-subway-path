package nextstep.subway.path;

import nextstep.subway.exception.SameSourceAndTargetException;
import nextstep.subway.exception.StationNotExistException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PathService {

    private final StationRepository stationRepository;
    private final LineService lineService;
    private final PathFinder pathFinder;

    public PathService(StationRepository stationRepository, LineService lineService, PathFinder pathFinder) {
        this.stationRepository = stationRepository;
        this.lineService = lineService;
        this.pathFinder = pathFinder;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        if(sourceId == targetId) {
            throw new SameSourceAndTargetException();
        }
        Station source = findStationById(sourceId);
        Station target = findStationById(targetId);
        List<Section> sections = lineService.getSections();

        return PathResponse.of(pathFinder.findPath(sections, source, target));
    }

    private Station findStationById(Long sourceId) {
        return stationRepository.findById(sourceId)
                .orElseThrow(() -> new StationNotExistException());
    }
}
