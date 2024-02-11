package nextstep.subway.path;

import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponseFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathService {
    private final PathFinder pathFinder;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository,
                       StationRepository stationRepository) {
        this.pathFinder = new JGraphPathFinder(lineRepository.findAll());
        this.stationRepository = stationRepository;
    }

    public PathResponse findShortCut(Long source, Long target) {
        Path path = pathFinder.shortcut(getStation(source), getStation(target));
        return new PathResponse(StationResponseFactory.createStationResponses(path.getStations()), path.getDistance());
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new IllegalArgumentException("해당 지하철역 정보를 찾지 못했습니다."));
    }
}
