package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.NotFoundLineException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, StationService stationService, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));

        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSection(upStation, downStation, request.getDistance());

        }
        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
            .map(this::createLineResponse)
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return createLineResponse(lineRepository.findById(id).orElseThrow(IllegalArgumentException::new));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        if (lineRequest.getName() != null) {
            line.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.setColor(lineRequest.getColor());
        }
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station requestUpStation = stationService.findById(sectionRequest.getUpStationId());
        Station requestDownStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        line.addSection(requestUpStation, requestDownStation, sectionRequest.getDistance());
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
        if (line.isSectionsEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getStations();

        return stations.stream().map(stationService::createStationResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Station station = stationService.findById(stationId);

        line.removeSection(station);
    }

    public LineSectionResponse getLineSections(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NotFoundLineException::new);
        List<SectionResponse> sectionResponse = sectionService.showSections(line);
        return new LineSectionResponse(line.getId(), line.getName(), line.getColor(), sectionResponse);
    }

    public List<LineSectionResponse> getAllLineSections() {
        List<Line> lines = lineRepository.findAll();
        List<LineSectionResponse> lineSectionResponses = new ArrayList<>();

        lines.forEach(line -> {
            List<SectionResponse> sectionResponse = sectionService.showSections(line);
            lineSectionResponses.add(new LineSectionResponse(line.getId(), line.getName(), line.getColor(), sectionResponse));
        });

        return lineSectionResponses;
    }

}
