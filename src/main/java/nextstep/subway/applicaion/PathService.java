package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import nextstep.subway.ui.PathController;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final PathFinder pathFinder;

    public PathService(LineRepository lineRepository, StationRepository stationRepository, PathFinder pathFinder) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.pathFinder = pathFinder;
    }

    public PathResponse showShortestPath(Long source, Long target) {
        List<Line> lines = lineRepository.findAll();
        Station startStation = stationRepository.findById(source)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 역의 경로를 조회할 수 없습니다."));
        Station endStation = stationRepository.findById(target)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 역의 경로를 조회할 수 없습니다."));
        return pathFinder.shortestPath(lines, startStation, endStation);
    }
}
