package nextstep.subway.path;

import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final PathFinder pathFinder;

    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.pathFinder = new PathFinder();
    }

    public PathResponse findPath(Long source, Long target) {
        Station sourceStation = stationRepository.findById(source).orElseThrow(() -> new SubwayException("역을 찾을 수 없습니다."));
        Station targetStation = stationRepository.findById(target).orElseThrow(() -> new SubwayException("역을 찾을 수 없습니다."));
        List<Line> lines = lineRepository.findAll();

        Path path = pathFinder.findPath(lines, sourceStation, targetStation);
        return new PathResponse(path);
    }
}
