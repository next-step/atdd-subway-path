package nextstep.subway.path.application;

import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.application.MapService;
import nextstep.subway.path.domain.PathMap;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final MapService mapService;

    public PathService(MapService mapService) {
        this.mapService = mapService;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(Long startStationId, Long endStationId, PathType type) {
        assertNotEqualsIds(startStationId, endStationId);

        final List<LineResponse> lineResponses = mapService.getMaps().getLineResponses();
        final List<LineStation> lineStations = mapStationIdToLineStations(lineResponses);

        final PathMap pathMap = PathMap.of(lineStations, type);

        final List<Long> shortestPathStationIds = pathMap.findDijkstraShortestPath(startStationId, endStationId);

        final List<LineStation> shortestPath = mapStationIdToLineStations(shortestPathStationIds, lineStations);

        final int distance = shortestPath.stream().mapToInt(LineStation::getDistance).sum();
        final int duration = shortestPath.stream().mapToInt(LineStation::getDuration).sum();

        return PathResponse.of(mapToLineStationResponses(shortestPathStationIds, lineResponses), distance, duration);
    }


    private List<LineStation> mapStationIdToLineStations(List<LineResponse> lineResponses) {
        return lineResponses.stream()
                .flatMap(line -> line.getStations().stream())
                .map(this::mapToLineStation)
                .collect(Collectors.toList());
    }

    private List<StationResponse> mapToLineStationResponses(List<Long> shortestPath, List<LineResponse> lines) {
        Map<Long, StationResponse> stationResponses = lines.stream()
                .flatMap(lineResponse -> lineResponse.getStations().stream())
                .map(LineStationResponse::getStation)
                .distinct()
                .collect(Collectors.toMap(StationResponse::getId, Function.identity()));

        return shortestPath.stream()
                .map(stationResponses::get)
                .collect(Collectors.toList());
    }

    private List<LineStation> mapStationIdToLineStations(List<Long> shortestPath, List<LineStation> lineStations) {
        List<LineStation> shortestPathLineStations = new ArrayList<>();
        for (int i = 1; i < shortestPath.size(); i++) {
            Long stationId = shortestPath.get(i);
            Long preStationId = shortestPath.get(i - 1);

            LineStation lineStation = lineStations.stream()
                    .filter(station -> Objects.equals(station.getStationId(), stationId))
                    .filter(station -> Objects.equals(preStationId, station.getPreStationId()))
                    .findAny()
                    .orElseThrow(RuntimeException::new);

            shortestPathLineStations.add(lineStation);
        }
        return shortestPathLineStations;
    }

    private LineStation mapToLineStation(LineStationResponse lineStationResponse) {
        return new LineStation(lineStationResponse.getStation().getId(), lineStationResponse.getPreStationId(), lineStationResponse.getDistance(), lineStationResponse.getDuration());
    }

    private void assertNotEqualsIds(Long startStationId, Long endStationId) {
        if (Objects.equals(startStationId, endStationId)) {
            throw new NotValidRequestException("출발역과 도착역은 같을 수 없습니다.");
        }
    }


}
