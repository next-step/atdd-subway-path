package nextstep.subway.path.application;

import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.application.MapService;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.path.domain.PathMap;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        MapResponse maps = mapService.getMaps();

        PathMap pathMap = PathMap.of(maps.getLineResponses());

        List<Long> shortestPath = pathMap.findDijkstraShortestPath(startStationId, endStationId);

        List<LineStationResponse> lineStations = maps.getLineResponses().stream()
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
