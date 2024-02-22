package nextstep.subway.line.addsection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineAddSectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineAddSectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineAddedSectionResponse addSection(Long lineId, LineAddSectionRequest request) {
        Line line = findLineByLineId(lineId);
        Station upStation = findStationByStationId(request.getUpStationId());
        Station downStation = findStationByStationId(request.getDownStationId());
        line.addNewSection(upStation, downStation, request.getDistance());
        return mapToResponse(line);
    }

    private Line findLineByLineId(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다. lineId: " + lineId));
    }

    private Station findStationByStationId(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다. stationId: " + stationId));
    }

    private LineAddedSectionResponse mapToResponse(Line line) {
        List<Station> stations = line.getAllStations();
        return new LineAddedSectionResponse(line.getId(), line.getName(), line.getColor(), mapToStationResponses(stations));
    }

    private List<LineAddedSectionStationResponse> mapToStationResponses(List<Station> stations) {
        return stations.stream()
                .map(station -> new LineAddedSectionStationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }
}
