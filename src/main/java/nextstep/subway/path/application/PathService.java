package nextstep.subway.path.application;

import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.application.MapService;
import nextstep.subway.path.domain.LineStations;
import nextstep.subway.path.domain.PathMap;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final MapService mapService;
    private final StationRepository stationRepository;

    public PathService(MapService mapService, StationRepository stationRepository) {
        this.mapService = mapService;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public PathResponse findPath(Long startStationId, Long endStationId, PathType type) {
        assertNotEqualsIds(startStationId, endStationId);

        final List<LineResponse> lineResponses = mapService.getMaps().getLineResponses();
        final List<LineStation> lineStations = mapStationIdToLineStations(lineResponses);

        final PathMap pathMap = PathMap.of(lineStations, type);
        final List<Long> pathStationIds = pathMap.findDijkstraPath(startStationId, endStationId);
        final LineStations pathLineStations = LineStations.from(pathStationIds, lineStations);

        return PathResponse.of(findAllStationByIds(pathStationIds), pathLineStations.getDistance(), pathLineStations.getDuration());
    }

    private List<StationResponse> findAllStationByIds(List<Long> shortestPathStationIds) {
        return stationRepository.findAllById(shortestPathStationIds).stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    private List<LineStation> mapStationIdToLineStations(List<LineResponse> lineResponses) {
        return lineResponses.stream()
                .flatMap(line -> line.getStations().stream())
                .map(this::mapToLineStation)
                .collect(Collectors.toList());
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
