package nextstep.subway.path;

import nextstep.subway.line.LineRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.station.Station;
import nextstep.subway.station.service.StationDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class PathService {

    private final StationDataService stationDataService;

    private final LineRepository lineRepository;

    public PathService(StationDataService stationDataService, LineRepository lineRepository) {
        this.stationDataService = stationDataService;
        this.lineRepository = lineRepository;
    }

    public PathResponse getPath(Long sourceStationId, Long targetStationId) {
        if (sourceStationId.equals(targetStationId)) {
            throw new PathException("출발역과 도착역이 같습니다.");
        }

        Station soruceStation = stationDataService.findStation(sourceStationId);
        Station targetStation = stationDataService.findStation(targetStationId);

        PathFinder pathFinder = new PathFinder();

        return pathFinder.getPath(getSectionsList(), soruceStation, targetStation);
    }

    private List<Sections> getSectionsList() {
        return lineRepository
                .findAll()
                .stream()
                .map(Line::getSections)
                .collect(Collectors.toList());
    }
}
