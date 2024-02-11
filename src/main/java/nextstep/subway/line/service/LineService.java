package nextstep.subway.line.service;

import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.presentation.request.CreateLineRequest;
import nextstep.subway.line.presentation.request.UpdateLineRequest;
import nextstep.subway.line.presentation.response.*;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public CreateLineResponse saveLine(CreateLineRequest createLineRequest) {
        Station upStation = stationRepository.findById(createLineRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundLineException());
        Station downStation = stationRepository.findById(createLineRequest.getDownStationId())
                .orElseThrow(() -> new NotFoundLineException());
        Section section = sectionRepository.save(Section.of(upStation, downStation, createLineRequest.getDistance()));

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
                .map(LineDto::from)
                .collect(Collectors.toList()));
    }

    public ShowLineResponse findLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundLineException());

        return ShowLineResponse.from(line);
    }

    @Transactional
    public UpdateLineResponse updateLine(Long lineId, UpdateLineRequest updateLineRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundLineException());

        line.updateLine(updateLineRequest.getColor());

        return UpdateLineResponse.from(line);
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public AddSectionResponse addSection(Long lineId, AddSectionRequest addSectionRequest) {
        Station upStation = stationRepository.findById(addSectionRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundLineException());
        Station downStation = stationRepository.findById(addSectionRequest.getDownStationId())
                .orElseThrow(() -> new NotFoundLineException());
        Section section = sectionRepository.save(Section.of(upStation, downStation, addSectionRequest.getDistance()));

        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundLineException());

        line.addSection(section);

        return AddSectionResponse.from(section);
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundLineException());
        Station deletedStation = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundStationException());

        line.deleteSection(deletedStation);
    }

}
