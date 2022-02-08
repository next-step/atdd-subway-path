package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.ExploreResponse;
import nextstep.subway.domain.*;
import nextstep.subway.handler.exception.StationException;
import org.springframework.stereotype.Service;

import java.util.List;

import static nextstep.subway.handler.exception.ErrorCode.STATION_NOT_FOUND_BY_ID;

@Service
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public ExploreResponse explore(Long source, Long target) {
        Station sourceStation = findStationById(source);
        Station targetStation = findStationById(target);

        PathFinder pathFinder = new PathFinder(findAllLines());

        return ExploreResponse.from(pathFinder.explore(sourceStation, targetStation));
    }

    private List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationException(STATION_NOT_FOUND_BY_ID));
    }
}
