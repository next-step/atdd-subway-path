package nextstep.subway.applicaion.query;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import nextstep.subway.exception.station.StationNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathQueryService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathQueryService(LineRepository lineRepository,
                            StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(long sourceId, long targetId) {
        Station sourceStation = findStationById(sourceId);
        Station targetStation = findStationById(targetId);
        ShortestPathChecker pathChecker = ShortestPathChecker.of(findAllLines());

        List<Station> path = pathChecker.findShortestPath(sourceStation, targetStation);
        return PathResponse.of(path);
    }

    private Station findStationById(long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationNotFoundException(id));
    }

    private List<Line> findAllLines() {
        return lineRepository.findAll();
    }

}
