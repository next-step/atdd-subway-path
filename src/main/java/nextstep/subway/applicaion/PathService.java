package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(final long sourceId, final long targetId) {
        PathFinder pathFinder = new PathFinder(getStation(sourceId), getStation(targetId), lineRepository.findAll());
        return new PathResponse(
            pathFinder.getPath().stream().map(station -> new StationResponse(station.getId(), station.getName())).collect(Collectors.toList()),
            pathFinder.getDistance()
        );
    }

    private Station getStation(final long sourceId) {
        return stationRepository.findById(sourceId).orElseThrow(() -> new DataIntegrityViolationException("존재하지 않은역 입니다."));
    }
}
