package nextstep.subway.line.application;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.AllStations;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.application.MapService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final MapService mapService;

    public LineService(LineRepository lineRepository, StationRepository stationRepository,
        MapService mapService) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.mapService = mapService;
    }

    public Line saveLine(LineRequest request) {
        return lineRepository.save(request.toLine());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        AllStations allStations = mapService.getAllLineStationsOfLines(new Lines(lines));

        return lines.stream()
            .map(line -> LineResponse.of(line, allStations))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        List<Long> stationIds = line.getStationInOrder().stream()
            .map(it -> it.getStationId())
            .collect(Collectors.toList());

        Map<Long, Station> stations = stationRepository.findAllById(stationIds).stream()
            .collect(Collectors.toMap(it -> it.getId(), Function.identity()));

        List<LineStationResponse> lineStationResponses = extractLineStationResponses(line, stations);

        return LineResponse.of(line, lineStationResponses);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(lineUpdateRequest.toLine());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private List<LineStationResponse> extractLineStationResponses(Line line, Map<Long, Station> stations) {
        return line.getStationInOrder().stream()
            .map(it -> LineStationResponse.of(it, StationResponse.of(stations.get(it.getStationId()))))
            .collect(Collectors.toList());
    }
}
