package subway.line.service;

import org.springframework.stereotype.Service;
import subway.line.entity.Line;
import subway.line.LineRepository;
import subway.line.dto.LineCreateRequest;
import subway.line.dto.LineResponse;
import subway.line.dto.LineUpdateRequest;
import subway.station.Station;
import subway.station.service.StationDataService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final LineDataService lineDataService;
    private final StationDataService stationDataService;

    public LineService(LineRepository lineRepository, LineDataService lineDataService, StationDataService stationDataService) {
        this.lineRepository = lineRepository;
        this.lineDataService = lineDataService;
        this.stationDataService = stationDataService;
    }

    public LineResponse saveLine(LineCreateRequest request) {
        Line line = new Line(request.getName(), request.getColor());

        Station upStation = stationDataService.findStation(request.getUpStationId());
        Station downStation = stationDataService.findStation(request.getDownStationId());

        line.generateSection(request.getDistance(), upStation, downStation);

        Line savedLine = lineRepository.save(line);

        return LineResponse.ofEntity(savedLine);
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream().map(LineResponse::ofEntity).collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineDataService.findLine(id);

        return LineResponse.ofEntity(line);
    }

    public void updateLine(Long id, LineUpdateRequest request) {
        Line line = lineDataService.findLine(id);

        line.updateLine(request.getName(), request.getColor());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
