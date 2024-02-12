package nextstep.subway.path;

import nextstep.subway.line.LineRepository;
import nextstep.subway.line.Lines;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponseFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathService {
    private final PathFinder pathFinder;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(PathFinder pathFinder,
                       LineRepository lineRepository,
                       StationRepository stationRepository) {
        this.pathFinder = pathFinder;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findShortCut(PathRequest pathRequest) {
        Path path = pathFinder.shortcut(Lines.from(lineRepository.findAllFetchJoin()),
                getStation(pathRequest.getSource()),
                getStation(pathRequest.getTarget()));
        return new PathResponse(StationResponseFactory.createStationResponses(path.getStations()), path.getDistance());
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new IllegalArgumentException("해당 지하철역 정보를 찾지 못했습니다."));
    }
}
