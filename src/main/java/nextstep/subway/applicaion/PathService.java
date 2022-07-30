package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(long sourceId, long targetId) {
        Station source = stationRepository.findById(sourceId).orElseThrow(IllegalArgumentException::new);
        Station target = stationRepository.findById(targetId).orElseThrow(IllegalArgumentException::new);
        PathFinder pathFinder = new PathFinder(source, target, lineRepository.findAll());
        return new PathResponse(
            pathFinder.getPath().stream().map(station -> new StationResponse(station.getId(), station.getName())).collect(Collectors.toList()),
            pathFinder.getDistance()
        );
    }
}
