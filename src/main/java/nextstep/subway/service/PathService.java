package nextstep.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.repository.LineRepository;

@Service
@AllArgsConstructor
public class PathService {
    private final StationService stationService;
    private final LineRepository lineRepository;

    public PathResponse getShortestPath(Long sourceId, Long targetId) {
        List<Line> allLines = lineRepository.findAll();
        Station sourceStation = stationService.findById(sourceId);
        Station targetStation = stationService.findById(targetId);
        Path path = new Path();
        return path.getShortestPath(allLines, sourceStation, targetStation);
    }
}
