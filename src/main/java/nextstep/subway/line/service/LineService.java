package nextstep.subway.line.service;

import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.presentation.request.CreateLineRequest;
import nextstep.subway.line.presentation.request.UpdateLineRequest;
import nextstep.subway.line.presentation.response.*;
import nextstep.subway.line.service.dto.ShowLineDto;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;
    private SectionService sectionService;

    public LineService(LineRepository lineRepository, StationService stationService, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional
    public CreateLineResponse saveLine(CreateLineRequest createLineRequest) {
        Station upStation = stationService.findById(createLineRequest.getUpStationId());
        Station downStation = stationService.findById(createLineRequest.getDownStationId());
        Section section = sectionService.save(Section.of(upStation, downStation, createLineRequest.getDistance()));

        Line line = lineRepository.save(
                Line.of(
                        createLineRequest.getName(), createLineRequest.getColor()
                )
        );

        line.addSection(section);

        return CreateLineResponse.from(line);
    }

    public ShowAllLinesResponse findAllLines() {
        return ShowAllLinesResponse.of(lineRepository.findAll().stream()
                .map(ShowLineDto::from)
                .collect(Collectors.toList()));
    }

    public ShowLineResponse findLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NotFoundLineException::new);

        return ShowLineResponse.from(line);
    }

    @Transactional
    public UpdateLineResponse updateLine(Long lineId, UpdateLineRequest updateLineRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NotFoundLineException::new);

        line.updateLine(updateLineRequest.getColor());

        return UpdateLineResponse.from(line);
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public AddSectionResponse addSection(Long lineId, AddSectionRequest addSectionRequest) {
        Station upStation = stationService.findById(addSectionRequest.getUpStationId());
        Station downStation = stationService.findById(addSectionRequest.getDownStationId());
        Section section = sectionService.save(Section.of(upStation, downStation, addSectionRequest.getDistance()));

        Line line = lineRepository.findById(lineId)
                .orElseThrow(NotFoundLineException::new);

        line.addSection(section);

        return AddSectionResponse.from(section);
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NotFoundLineException::new);
        Station deletedStation = stationService.findById(stationId);

        line.deleteSection(deletedStation);
    }

}
