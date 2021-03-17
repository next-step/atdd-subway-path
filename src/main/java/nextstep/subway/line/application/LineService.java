package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.NoSuchLineException;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        final Station upStation = stationService.findById(request.getUpStationId());
        final Station downStation = stationService.findById(request.getDownStationId());
        final Line persistLine = lineRepository.save(new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
        return createLineResponse(persistLine);
    }

    public List<LineResponse> findLineResponses() {
        final List<Line> persistLines = lineRepository.findAll();
        return persistLines.stream()
                .map(line -> createLineResponse(line))
                .collect(Collectors.toList());
    }

    public List<Line> findAllLines(){
        return lineRepository.findAll();
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(()-> new NoSuchLineException("존재하지 않는 노선입니다"));
    }

    public LineResponse findLineResponseById(Long id) {
        final Line persistLine = findLineById(id);
        return createLineResponse(persistLine);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        final Line persistLine = lineRepository.findById(id).orElseThrow(()-> new NoSuchLineException("존재하지 않는 노선입니다"));
        persistLine.update(new Line(lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest request) {
        final Line line = findLineById(lineId);
        final Station upStation = stationService.findStationById(request.getUpStationId());
        final Station downStation = stationService.findStationById(request.getDownStationId());
        addSection(line, upStation, downStation, request.getDistance());
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance){
        line.addSection(upStation, downStation, distance);
    }

    public void removeSection(Long lineId, Long stationId) {
        final Line line = findLineById(lineId);
        removeSection(line, stationId);
    }

    public void removeSection(Line line, Long stationId) {
        final Station station = stationService.findStationById(stationId);
        line.removeSection(station);
    }

    public LineResponse createLineResponse(Line line) {
        final List<StationResponse> stations = line.getStations().stream()
                .map(it -> StationResponse.of(it))
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations, line.getCreatedDate(), line.getModifiedDate());
    }
}
