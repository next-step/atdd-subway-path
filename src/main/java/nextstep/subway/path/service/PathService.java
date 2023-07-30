package nextstep.subway.path.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathDto;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
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

    public PathDto findPath(Long source, Long target) {
        Station startStation = stationRepository.findById(source)
                .orElseThrow(() -> new IllegalArgumentException("출발역이 존재하지 않습니다."));
        Station endStation = stationRepository.findById(target)
                .orElseThrow(() -> new IllegalArgumentException("도착역이 존재하지 않습니다."));

        List<Line> lines = lineRepository.findAll();
        PathFinder pathFinder = new PathFinder(lines);
        pathFinder.findPath(startStation, endStation);
        return null;
    }
}
