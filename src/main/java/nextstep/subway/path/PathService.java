package nextstep.subway.path;

import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {

        return new PathResponse(Collections.emptyList(), 0);
    }
}
