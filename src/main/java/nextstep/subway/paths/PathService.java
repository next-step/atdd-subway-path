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

    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public Path findPath(Long source, Long target) {
        List<Line> lines = lineRepository.findAll();
        Station startStation = this.stationRepository.findById(source).orElseThrow(() -> new IllegalArgumentException("출발역이 존재하지 않습니다."));
        Station endStation = this.stationRepository.findById(target).orElseThrow(() -> new IllegalArgumentException("도착역이 존재하지 않습니다."));
        return new PathFinder(lines).findPath(startStation, endStation);
    }
}
