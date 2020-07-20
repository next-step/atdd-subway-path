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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MapService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public MapService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public MapResponse findAllMaps() {
        List<LineResponse> lineResponses = new ArrayList<>();
        List<Line> lines = lineRepository.findAll();

        for (Line line : lines) {
            List<LineStation> lineStations = line.getStationInOrder();
            List<Station> stations = stationRepository.findAllById(getStationIds(lineStations));

            List<LineStationResponse> lineStationResponses = getLineStationResponses(lineStations, stations);
            lineResponses.add(LineResponse.of(line, lineStationResponses));
        }

        return new MapResponse(lineResponses);
    }

    private List<LineStationResponse> getLineStationResponses(List<LineStation> lineStations, List<Station> stations) {
        return lineStations.stream()
                .map(it -> getLineStationResponse(stations, it))
                .collect(Collectors.toList());
    }

    private List<Long> getStationIds(List<LineStation> lineStations) {
        return lineStations.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
    }

    private LineStationResponse getLineStationResponse(List<Station> stations, LineStation it) {
        Optional<Station> station = stations.stream()
                .filter(st-> st.getId() == it.getStationId())
                .findFirst();
        return LineStationResponse.of(it, StationResponse.of(station.orElseThrow(RuntimeException::new)));
    }
}
