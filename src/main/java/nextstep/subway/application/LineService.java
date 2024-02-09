package nextstep.subway.application;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Sections;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.repository.LineRepository;
import nextstep.subway.entity.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final StationRepository stationRepository;

    private final LineRepository lineRepository;

    public LineService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest request) {
        Line line = convertToLineEntity(request);
        line.addSection(convertToSectionEntity(request, line));
        return convertToLineResponse(lineRepository.save(line));
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(this::convertToLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long lineId) {
        return convertToLineResponse(lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new));
    }

    @Transactional
    public void updateLine(Long lineId, LineRequest request) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);
        convertToLineResponse(updateLine(request, line));
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(IllegalArgumentException::new);
    }

    private Line updateLine(LineRequest request, Line line) {
        return line.update(
                request.getName(),
                request.getColor()
        );
    }

    private Line convertToLineEntity(LineRequest request) {
        return new Line(
                request.getName(),
                request.getColor(),
                request.getDistance());
    }

    private LineResponse convertToLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                convertToStationResponses(line.getSections()));
    }

    private Section convertToSectionEntity(LineRequest request, Line line) {
        return new Section(
                findStation(request.getUpStationId()),
                findStation(request.getDownStationId()),
                request.getDistance(),
                line);
    }

    private List<StationResponse> convertToStationResponses(Sections sections) {
        List<StationResponse> stationResponses = new ArrayList<>();
        stationResponses.add(convertToStationResponse(sections.findFirstStation()));
        sections.getSections()
                .forEach(section ->
                        stationResponses.add(convertToStationResponse(section.getDownStation())));

        return stationResponses;
    }

    private StationResponse convertToStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}
