package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MapService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public MapService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public MapResponse loadMap() {
        List<Line> lines = this.lineRepository.findAll();
        Map<Long, Station> stations = this.getAllStationsOfLines(lines);

        List<LineResponse> lineResponses = this.toLineResponses(lines, stations);
        return MapResponse.of(lineResponses);
    }

    private Map<Long, Station> getAllStationsOfLines(List<Line> lines) {
        List<Long> allStationIdOfLines = lines.stream()
                .flatMap(it -> it.getLineStations().getLineStations().stream().map(LineStation::getStationId))
                .distinct()
                .collect(Collectors.toList());

        return this.stationRepository.findAllById(allStationIdOfLines).stream()
                .collect(Collectors.toMap(Station::getId, it -> it));
    }

    private List<LineResponse> toLineResponses(List<Line> lines, Map<Long, Station> stations) {
        return lines.stream()
                .map(line -> this.toLineResponse(line, stations))
                .collect(Collectors.toList());
    }

    private LineResponse toLineResponse(Line line, Map<Long, Station> stations) {
        List<LineStationResponse> lineStationResponses = line.getLineStations().getStationsInOrder().stream()
                .map(it -> LineStationResponse.of(it, StationResponse.of(stations.get(it.getStationId()))))
                .collect(Collectors.toList());

        return LineResponse.of(line, lineStationResponses);
    }
}
