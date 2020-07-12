package nextstep.subway.map.ui;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/maps")
public class MapController {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public MapController(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @GetMapping
    public ResponseEntity<MapResponse> hello() {
        List<Line> lines = this.lineRepository.findAll();

        final List<LineResponse> lineResponses = lines.parallelStream().map(line -> {
            List<Long> stationIds = line.getStationInOrder().stream()
                    .map(it -> it.getStationId())
                    .collect(Collectors.toList());

            Map<Long, Station> stations = stationRepository.findAllById(stationIds).stream()
                    .collect(Collectors.toMap(it -> it.getId(), Function.identity()));

            return LineResponse.of(line, this.extractLineStationResponses(line, stations));
        }).collect(Collectors.toList());

        return ResponseEntity.ok()
                .body(new MapResponse(lineResponses));
    }

    private List<LineStationResponse> extractLineStationResponses(Line line, Map<Long, Station> stations) {
        return line.getStationInOrder().stream()
                .map(it -> LineStationResponse.of(it, StationResponse.of(stations.get(it.getStationId()))))
                .collect(Collectors.toList());
    }
}
