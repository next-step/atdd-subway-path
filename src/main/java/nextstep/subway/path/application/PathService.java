package nextstep.subway.path.application;

import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.application.MapService;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.path.domain.PathMap;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final MapService mapService;

    public PathService(MapService mapService) {
        this.mapService = mapService;
    }

    @Transactional(readOnly = true)
    public PathResponse findShortestPath(Long startStationId, Long endStationId) {
        assertNotEqualsIds(startStationId, endStationId);

        List<LineResponse> lineResponses = mapService.getMaps().getLineResponses();
        PathMap pathMap = PathMap.of(lineResponses);

        List<Long> shortestPath = pathMap.findDijkstraShortestPath(startStationId, endStationId);

        List<LineStationResponse> lineStations = lineResponses.stream()
                .flatMap(line -> line.getStations().stream())
                .collect(Collectors.toList());

        return PathResponse.of(shortestPath, lineStations);
    }

    private void assertNotEqualsIds(Long startStationId, Long endStationId) {
        if (Objects.equals(startStationId, endStationId)) {
            throw new NotValidRequestException("출발역과 도착역은 같을 수 없습니다.");
        }
    }
}
