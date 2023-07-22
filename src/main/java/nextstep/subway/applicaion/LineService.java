package nextstep.subway.applicaion;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
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
        line.getSections().add(new Section(line, upStation, downStation, request.getDistance()));

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
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(LineException::new);
        line.setName(lineRequest.getName());
        line.setColor(lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository
                .findById(lineId)
                .orElseThrow(LineException::new);

        if (addSectionByStationPoint(line, sectionRequest)) { return; }

        log.info("[LineService] Add section at end point");
        line.getSections().add(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    @Transactional
    private boolean addSectionByStationPoint(Line line, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        int requestedDistance = sectionRequest.getDistance();

        // 기준 - 지하철 노선에 역이 2개만 존재하는 경우
        List<Section> savedSections = line.getSections();
        Section startSection = savedSections.get(0);
        Station startStation = startSection.getUpStation();
        Station endStation = startSection.getDownStation();

        if (upStation.getId() == startStation.getId()) {
            log.info("[LineService] Add section between saved section");
            int calculatedDistance = startSection.getDistance() - requestedDistance;
            startSection.updateSection(upStation, downStation, calculatedDistance);
            line.getSections().add(new Section(line, upStation, endStation, calculatedDistance));
            return true;
        } else if (downStation.getId() == startStation.getId()) {
            log.info("[LineService] Add Section at starting point");
            startSection.updateSection(upStation, startStation, requestedDistance);
            line.getSections().add(new Section(line, downStation, endStation, startSection.getDistance()));
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(LineException::new);
        Station station = stationService.findById(stationId);

        if (!line.getSections().get(line.getSections().size() - 1).getDownStation().equals(station)) {
            throw new SectionException();
        }

        line.getSections().remove(line.getSections().size() - 1);
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