package nextstep.subway.applicaion;


import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public PathResponse showPaths(Long source, Long target) {
        List<Line> lines = lineRepository.findAll();
        Map<Long, Station> stationMap = stationRepository.findAll().stream().collect(Collectors.toMap(Station::getId, Function.identity()));

        return PathFinder.create(lines, stationMap).findPath(source, target);
    }
}
