package nextstep.subway.paths;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final PathFinder pathFinder;

    public PathService(StationRepository stationRepository, LineRepository lineRepository, PathFinder pathFinder) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.pathFinder = pathFinder;
    }

    public Path findPath(Long source, Long target) {
        List<Line> lines = this.lineRepository.findAll();
        Station startStation = this.stationRepository.findById(source).orElseThrow(() -> new IllegalArgumentException("출발역이 존재하지 않습니다."));
        Station endStation = this.stationRepository.findById(target).orElseThrow(() -> new IllegalArgumentException("도착역이 존재하지 않습니다."));
        return this.pathFinder.findPath(lines, startStation, endStation);
    }
}
