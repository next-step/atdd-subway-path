package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (!request.isSectionNull()) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSections(new Section(line, upStation, downStation, request.getDistance()));
        }

        return createLineResponse(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return createLineResponse(getLine(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.updateLine(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = getLine(lineId);

        line.addSections(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        Station station = stationService.findById(stationId);
        line.removeSection(station);
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                createStationResponses(line),
                line.getCreatedDate(),
                line.getModifiedDate()
        );
    }

    private List<StationResponse> createStationResponses(Line line) {
        return line.getStations()
                .stream()
                .map(it -> stationService.createStationResponse(it))
                .collect(Collectors.toList());
    }
}
