package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import nextstep.subway.error.exception.BusinessException;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;

import java.util.List;

import static nextstep.subway.error.exception.ErrorCode.STATION_NOT_FOUND;

@Service
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        final Station sourceStation = getStation(sourceStationId);
        final Station targetStation = getStation(targetStationId);
        final List<Line> lines = lineRepository.findAll();
        final PathFinder pathFinder = new PathFinder(lines);
        final GraphPath path = pathFinder.findPath(sourceStation, targetStation);

        return new PathResponse(path.getVertexList(), (int) path.getWeight());
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new BusinessException(STATION_NOT_FOUND));
    }
}
