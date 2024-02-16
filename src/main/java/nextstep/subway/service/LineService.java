package nextstep.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.controller.dto.*;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.domain.repository.SectionRepository;
import nextstep.subway.exception.CheckDuplicateStationException;
import nextstep.subway.exception.InvalidUpStationException;
import nextstep.subway.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.controller.dto.LineResponse.lineToLineResponse;
import static nextstep.subway.controller.dto.SectionResponse.sectionToSectionResponse;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
        Station upStation = stationService.getStationById(lineCreateRequest.getUpStationId());
        Station downStation = stationService.getStationById(lineCreateRequest.getDownStationId());

        Line line = Line.builder()
                .name(lineCreateRequest.getName())
                .color(lineCreateRequest.getColor())
                .upStation(upStation)
                .downStation(downStation)
                .distance(lineCreateRequest.getDistance())
                .build();

        line = lineRepository.save(line);

        return lineToLineResponse(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest request) {
        Line findLine = getLineById(id);
        findLine.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        Line findLine = getLineById(id);
        lineRepository.deleteById(findLine.getId());
    }

    public List<LineResponse> findAllLine() {
        return lineRepository.findAll().stream()
                .map(LineResponse::lineToLineResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectionResponse addSection(Long lineId, SectionCreateRequest request) {
        Station upStation = stationService.getStationById(Long.valueOf(request.getUpStationId()));
        Station downStation = stationService.getStationById(Long.valueOf(request.getDownStationId()));

        Line line = getLineById(lineId);

        Section section = Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(request.getDistance())
                .build();
        line.addSection(section);

        return sectionToSectionResponse(section);
    }

    @Transactional
    public void removeSection(Long lineId, Long stationId) {
        Line line = getLineById(lineId);
        line.removeSection(stationId);
    }

    public LineResponse findLineById(Long id) {
        Line findLine = getLineById(id);
        return lineToLineResponse(findLine);
    }

    public Line getLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("지하철 노선이 존재하지 않습니다."));
    }

}
