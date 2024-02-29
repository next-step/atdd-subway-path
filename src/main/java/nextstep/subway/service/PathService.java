package nextstep.subway.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathCalculator;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.repository.LineRepository;

@Service
@Slf4j
@AllArgsConstructor
public class PathService {
    private static final Map<String, PathResponse> PATH_CACHE = new ConcurrentHashMap<>();
    private final StationService stationService;
    private final LineRepository lineRepository;

    public PathResponse getShortestPath(Long sourceId, Long targetId) {
        if (PATH_CACHE.containsKey(getKey(sourceId, targetId))) {
            //cache hit
            return PATH_CACHE.get(getKey(sourceId, targetId));
        }
        List<Line> allLines = lineRepository.findAll();
        Station sourceStation = stationService.findById(sourceId);
        Station targetStation = stationService.findById(targetId);

        PathCalculator pathCalculator = new PathCalculator(allLines);
        Path shortestPath = pathCalculator.getShortestPath(sourceStation, targetStation);

        PathResponse pathResponse = PathResponse.of(shortestPath);
        PATH_CACHE.put(getKey(sourceId, targetId), pathResponse); // cache miss
        return pathResponse;
    }

    private String getKey(Long sourceId, Long targetId) {
        return sourceId.toString() + ":" + targetId.toString();
    }
}
