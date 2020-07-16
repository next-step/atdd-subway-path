package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class MapService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public MapService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public MapResponse findAllLineAndStation() {
        List<Line> lines = lineRepository.findAll();
        List<Long> stationIds = findStationIds(lines);
        Map<Long, Station> findStations = findStationsByIds(stationIds);

        List<LineResponse> response = getLineResponse(lines, findStations);

        return MapResponse.of(response);
    }

    private List<LineResponse> getLineResponse(List<Line> lines, Map<Long, Station> findStations) {
        return lines.stream()
                .map(it -> LineResponse.of(it, extractLineStationResponses(it, findStations)))
                .collect(Collectors.toList());
    }

    private Map<Long, Station> findStationsByIds(List<Long> stationIds) {
        return stationRepository.findAllById(stationIds).stream()
                    .collect(Collectors.toMap(it -> it.getId(), Function.identity()));
    }

    private List<Long> findStationIds(List<Line> lines) {
        return lines.stream()
                .flatMap(it -> it.getStationInOrder().stream())
                .map(it -> it.getStationId())
                .collect(Collectors.toList());
    }

    private List<LineStationResponse> extractLineStationResponses(Line line, Map<Long, Station> stations) {
        return line.getStationInOrder().stream()
                .map(it -> LineStationResponse.of(it, StationResponse.of(stations.get(it.getStationId()))))
                .collect(Collectors.toList());
    }
}
