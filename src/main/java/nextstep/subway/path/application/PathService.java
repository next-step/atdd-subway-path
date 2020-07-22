package nextstep.subway.path.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.ShortestPathResult;
import nextstep.subway.station.dto.StationResponse;

@Service
public class PathService {

    private final ShortestPathFinder shortestPathFinder;
    private final LineService lineService;

    public PathService(ShortestPathFinder shortestPathFinder, LineService lineService) {
        this.shortestPathFinder = shortestPathFinder;
        this.lineService = lineService;
    }

    public PathResponse findShortestPath(Long startId, Long endId, ShortestPathSearchType type) {
        List<LineResponse> allLines = lineService.findAllLines();
        ShortestPathResult result =
            Optional.ofNullable(shortestPathFinder.findShortestPath(allLines, startId, endId, type))
                .orElse(ShortestPathResult.empty());
        List<StationResponse> stations = result.getStations();
        int totalDuration = totalDurationCalculator(result, allLines);
        int totalDistance = totalDistanceCalculator(result, allLines);

        return PathResponse.of(stations, result.getWeight(), totalDuration, totalDistance);
    }

    private int totalDurationCalculator(ShortestPathResult result, List<LineResponse> allLines) {
        List<LineStationResponse> lineStationResponses = result.toLineStationResponses(allLines);
        return lineStationResponses.stream()
            .skip(1) // 자기 자신은 제외
            .mapToInt(LineStationResponse::getDuration)
            .sum();
    }

    private int totalDistanceCalculator(ShortestPathResult result, List<LineResponse> allLines) {
        List<LineStationResponse> lineStationResponses = result.toLineStationResponses(allLines);
        return lineStationResponses.stream()
            .skip(1) // 자기 자신은 제외
            .mapToInt(LineStationResponse::getDuration)
            .sum();
    }
}
