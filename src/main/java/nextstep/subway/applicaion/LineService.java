package nextstep.subway.applicaion;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.applicaion.exception.domain.LineException;
import nextstep.subway.applicaion.exception.domain.SectionException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        line.addSection(new Section(line, upStation, downStation, request.getDistance()));

        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return createLineResponse(lineRepository.findById(id).orElseThrow(LineException::new));
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(LineException::new);
        line.updateNameAndColor(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public Line addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository
                .findById(lineId)
                .orElseThrow(LineException::new);
        Section newSection = new Section(line, upStation, downStation, sectionRequest.getDistance());

        line.beforeAddSection(newSection);
        return addSectionByType(line, newSection);
    }

    private Line addSectionByType(Line line, Section newSection) {
        Station startStation = line.getStartStation();
        Station endStation = line.getLastStation();

        if (newSection.getDownStation().checkEqualStation(startStation)) {
            log.info("[LineService] Add Section at start point");
            return line.addSectionAtStart(newSection);
        } else if (newSection.getUpStation().checkEqualStation(endStation)) {
            log.info("[LineService] Add section at end point");
            return line.addSectionAtEnd(newSection);
        }
        log.info("[LineService] Add section at middle");
        return line.addSectionAtMiddle(newSection);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(LineException::new);
        Station station = stationService.findById(stationId);

        if (!line.getSections().get(line.getSections().size()-1).getDownStation().equals(station)) {
            throw new SectionException("Check requested stationId or lineId.");
        }
        line.getSections().remove(line.getSections().size()-1);
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
        if (line.getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, line.getSections().get(0).getUpStation());

        return stations.stream()
                .map(it -> stationService.createStationResponse(it))
                .collect(Collectors.toList());
    }
}