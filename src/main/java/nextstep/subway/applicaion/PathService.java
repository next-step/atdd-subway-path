package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private PathFinder pathFinder;

    public PathService(LineRepository lineRepository, StationRepository stationRepository, PathFinder pathFinder) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.pathFinder = pathFinder;
    }

    public PathResponse findPath(Long source, Long target) {
        List<Line> lines = lineRepository.findAll();

        pathFinder.initGraph(lines);
        Path path = pathFinder.searchShortPath(source, target);

        return createPathResponse(path);
    }

    private PathResponse createPathResponse(Path path) {
        List<Station> stations = path.getStationIds().stream()
                .map(a -> stationRepository.findById(a).filter(station -> null != station).get())
                .collect(Collectors.toList());

        return new PathResponse(stations, path.getDistance());
    }
}
