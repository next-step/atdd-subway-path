package nextstep.subway.line.service;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.controller.dto.LineRequest;
import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.line.controller.dto.LineUpdateRequest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.infra.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionStations;
import nextstep.subway.station.controller.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final StationService stationService;
    private final LineRepository lineRepository;

    public LineService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upwardStation = stationService.getStation(lineRequest.getUpStationId());
        Station downwardStation = stationService.getStation(lineRequest.getDownStationId());
        Section section = new Section(new SectionStations(upwardStation, downwardStation), lineRequest.getDistance());
        Line savedLine = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor(), section));
        return createLineResponse(savedLine);
    }

    public List<Line> getAllLines() {
        return lineRepository.findAll();
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = getAllLines();
        return createLineResponseList(lines);
    }

    public LineResponse getLineResponse(Long lineId) {
        Line line = getLine(lineId);
        return createLineResponse(line);
    }

    @Transactional
    public void updateLine(Long lineId, LineUpdateRequest updateRequest) {
        Line line = getLine(lineId);
        line.updateName(updateRequest.getName());
        line.updateColor(updateRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    public Line getLine(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow();
    }

    private List<LineResponse> createLineResponseList(List<Line> lines) {
        return lines.stream().map(this::createLineResponse).collect(Collectors.toList());
    }

    private LineResponse createLineResponse(Line line) {
        List<Station> stations = line.getStationsByOrder();
        List<StationResponse> stationResponses = stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }
}
