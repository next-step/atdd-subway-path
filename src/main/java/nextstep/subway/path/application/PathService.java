package nextstep.subway.path.application;

import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.path.domain.PathMap;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Transactional(readOnly = true)
    public PathResponse findShortestPath(Long startStationId, Long endStationId) {
        assertNotEqualsIds(startStationId, endStationId);

        List<Line> allLines = lineService.findAllLineEntities();
        PathMap pathMap = PathMap.of(allLines);

        List<Long> shortestPath = pathMap.findDijkstraShortestPath(startStationId, endStationId);

        List<LineStation> lineStations = allLines.stream()
                .flatMap(line -> line.getStationInOrder().stream())
                .collect(Collectors.toList());

        long distance = 0;
        long duration = 0;

        for (int i = 1; i < shortestPath.size(); i++) {
            Long stationId = shortestPath.get(i);
            Long preStationId = shortestPath.get(i - 1);

            LineStation lineStation = lineStations.stream()
                    .filter(station -> Objects.equals(station.getStationId(), stationId))
                    .filter(station -> Objects.equals(preStationId, station.getPreStationId()))
                    .findAny()
                    .orElseThrow(RuntimeException::new);

            distance += lineStation.getDistance();
            duration += lineStation.getDuration();
        }

        return PathResponse.of(stationService.findAllById(shortestPath), distance, duration);
    }

    private void assertNotEqualsIds(Long startStationId, Long endStationId) {
        if (Objects.equals(startStationId, endStationId)) {
            throw new NotValidRequestException("출발역과 도착역은 같을 수 없습니다.");
        }
    }
}
