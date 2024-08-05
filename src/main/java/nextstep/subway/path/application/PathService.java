package nextstep.subway.path.application;

import java.util.List;
import lombok.AllArgsConstructor;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class PathService {

    private final PathFinder pathFinder;
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        Station source = stationRepository.findByIdOrThrow(sourceId);
        Station target = stationRepository.findByIdOrThrow(targetId);
        List<Line> lines = lineRepository.findAll();

        return pathFinder.find(lines, source, target);
    }
}
