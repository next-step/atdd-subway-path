package subway.line.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.SubwayNotFoundException;
import subway.line.constant.SubwayMessage;
import subway.line.dto.LineCreateRequest;
import subway.line.dto.LineModifyRequest;
import subway.line.dto.LineResponse;
import subway.line.dto.SectionCreateRequest;
import subway.line.dto.SectionDeleteRequest;
import subway.line.model.Line;
import subway.line.model.Section;
import subway.line.repository.LineRepository;
import subway.station.model.Station;
import subway.station.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LineService {


    private final LineRepository lineRepository;

    private final StationService stationService;

    @Transactional
    public Line saveLine(LineCreateRequest createRequest,
                         Station upStation,
                         Station downStation) {
        Line request = LineCreateRequest.to(createRequest, upStation, downStation);
        return lineRepository.save(request);
    }

    @Transactional
    public Line saveLine(Line line) {
        return lineRepository.save(line);
    }

    @Transactional
    public void updateLine(Long id, LineModifyRequest request) {
        Line line = this.findLineById(id);
        line.updateLine(request.getName(), request.getColor());
    }

    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::from)
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.LINE_NOT_FOUND_MESSAGE));
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.LINE_NOT_FOUND_MESSAGE));
    }

    @Transactional
    public void deleteById(Long id) {
        lineRepository.findById(id)
                .orElseThrow(() -> new SubwayNotFoundException(SubwayMessage.LINE_NOT_FOUND_MESSAGE));
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse createLine(LineCreateRequest lineRequest) {
        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());

        Line line = LineCreateRequest.to(lineRequest, upStation, downStation);
        lineRepository.save(line);

        Section section = Section.builder()
                .downStation(downStation)
                .upStation(upStation)
                .distance(lineRequest.getDistance())
                .build();
        line.addSection(section);
        return LineResponse.from(line);
    }

    @Transactional
    public void appendSection(final Long lineId, SectionCreateRequest request) {
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        Line foundLine = this.findLineById(lineId);

        Section section = Section.builder()
                .downStation(downStation)
                .upStation(upStation)
                .distance(request.getDistance())
                .build();
        foundLine.addSection(section);
    }

    @Transactional
    public void deleteSection(SectionDeleteRequest request) {
        Line foundLine = this.findLineById(request.getLineId());

        Station station = stationService.findStationById(request.getStationId());
        foundLine.deleteSectionByStation(station);
    }
}
