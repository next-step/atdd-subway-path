package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.sectioncondition.add.SectionAddCondition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionAddCondition sectionAddCondition;

    public LineService(LineRepository lineRepository, StationService stationService, final SectionAddCondition sectionAddCondition) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionAddCondition = sectionAddCondition;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (request.hasSectionData()) {
            addSection(line, request.getUpStationId(), request.getDownStationId(), request.getDistance());
        }
        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return createLineResponse(findLine(id));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLine(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest request) {
        Line line = findLine(lineId);
        addSection(line, request.getUpStationId(), request.getDownStationId(), request.getDistance());
    }

    private void addSection(final Line line, final Long upStationId, final Long downStationId, final int distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        final AddSectionRequest addSectionRequest = new AddSectionRequest(upStation, downStation, distance);
        if (sectionAddCondition.matches(line, addSectionRequest)) {
            line.addSection(sectionAddCondition, addSectionRequest);
        }

    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                createStationResponses(line)
        );
    }

    private List<StationResponse> createStationResponses(Line line) {
        List<Station> stations = line.getStations();
        return stations.stream()
                .map(stationService::createStationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLine(lineId);

        if (!line.isLastDownStation(stationService.findById(stationId))) {
            throw new IllegalArgumentException();
        }

        line.removeLastSection();
    }

    private Line findLine(final Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(IllegalArgumentException::new);
    }

}
