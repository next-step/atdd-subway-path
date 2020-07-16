package nextstep.subway.map.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import nextstep.subway.line.domain.AllStations;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.line.dto.LineResponses;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
public class MapService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public MapService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public MapResponse responseMap() {
        List<Line> lineList = lineRepository.findAll();
        Lines lines = new Lines(lineList);
        AllStations stations = getAllLineStationsOfLines(lines);
        LineResponses lineResponses = lines.toLineResponses(stations);
        return new MapResponse(lineResponses);
    }

    private AllStations getAllLineStationsOfLines(Lines lines) {
        List<Long> allStationIdOfLines = lines.getAllStationIdsOfLines();
        return stationRepository.findAllById(allStationIdOfLines).stream()
            .collect(Collectors.collectingAndThen(Collectors.toMap(Station::getId, station -> station),
                AllStations::new));
    }
}
