package nextstep.subway.line.service;

import nextstep.subway.line.dto.CreateLineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.line.dto.ModifyLineRequest;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.LINE_NOT_FOUND;
import static nextstep.subway.converter.LineConverter.convertToLineResponseByLine;
import static nextstep.subway.converter.LineConverter.convertToLineResponseByLineAndStations;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(final CreateLineRequest createLineRequest) {
        Station upStation = stationService.getStationByIdOrThrow(createLineRequest.getUpStationId());
        Station downStation = stationService.getStationByIdOrThrow(createLineRequest.getDownStationId());

        Section section = Section.of(upStation, downStation, createLineRequest.getDistance());

        Sections sections = new Sections();
        sections.addSection(section);

        Line line = Line.of(createLineRequest.getName(), createLineRequest.getColor(), createLineRequest.getDistance(), sections);
        lineRepository.save(line);

        StationResponse upStationResponse = new StationResponse(upStation.getId(), upStation.getName());
        StationResponse downStationResponse = new StationResponse(downStation.getId(), downStation.getName());

        LineResponse lineResponse = convertToLineResponseByLineAndStations(line, List.of(upStationResponse, downStationResponse));

        return lineResponse;
    }

    @Transactional(readOnly = true)
    public LinesResponse findAllLines() {
        List<Line> lines = lineRepository.findAll();
        LinesResponse linesResponse = new LinesResponse();
        for (Line line : lines) {
            linesResponse.addLineResponse(convertToLineResponseByLine(line));
        }

        return linesResponse;
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
        Line line = getLineByIdOrThrow(id);

        return convertToLineResponseByLine(line);
    }

    @Transactional
    public void modifyLine(final Long id, final ModifyLineRequest modifyLineRequest) {
        Line line = getLineByIdOrThrow(id);
        line.changeName(modifyLineRequest.getName());
        line.changeColor(modifyLineRequest.getColor());

        lineRepository.save(line);
    }

    @Transactional
    public void deleteLine(final Long id) {
        Line line = getLineByIdOrThrow(id);
        lineRepository.delete(line);
    }

    public Line getLineByIdOrThrow(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(String.valueOf(LINE_NOT_FOUND)));
    }

    public Line saveLine(Line line) {
        return lineRepository.save(line);
    }

    public List<Line> getAllLines() {
        return lineRepository.findAll();
    }

}
