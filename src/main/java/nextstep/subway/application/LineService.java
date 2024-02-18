package nextstep.subway.application;

import nextstep.subway.dto.*;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.repository.LineRepository;
import nextstep.subway.entity.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

    public LineResponse findLineWithConvertResponse(Long lineId) {
        return convertToLineResponse(findLineById(lineId));
    }

    @Transactional
    public SectionResponse addSection(SectionRequest request) {
        Line line = findLineById(request.getLineId());
        Section section = convertSectionRequestToEntity(request, line);

        return convertToSectionResponse(section.setLine(line));
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationIdToDelete) {
        Line line = findLineById(lineId);
        line.delete(findStation(stationIdToDelete));
    }

    public Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("노선 번호에 해당하는 노선이 없습니다."));
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("역 번호에 해당하는 역이 없습니다."));
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
                request.getColor());
    }

    private LineResponse convertToLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                convertStationResponses(line.getAllSections()));
    }

    private List<StationResponse> convertStationResponses(List<Section> sections) {
        Set<StationResponse> stationResponses = new LinkedHashSet<>();

        sections.forEach(section -> {
                    stationResponses.add(convertToStationResponse(section.getUpStation()));
                    stationResponses.add(convertToStationResponse(section.getDownStation()));
                });

        return new ArrayList<>(stationResponses);
    }


    private Section convertToSectionEntity(LineRequest request, Line line) {
        return new Section(
                findStation(request.getUpStationId()),
                findStation(request.getDownStationId()),
                request.getDistance(),
                line);
    }

    private StationResponse convertToStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }

    private SectionResponse convertToSectionResponse(Section section) {
        return new SectionResponse(
                section.getId(),
                findStation(section.getUpStation().getId()),
                findStation(section.getDownStation().getId()),
                section.getDistance()
        );
    }

    private Section convertSectionRequestToEntity(SectionRequest request, Line line) {
        return new Section(
                findStation(request.getUpStationId()),
                findStation(request.getDownStationId()),
                request.getDistance(),
                line
        );
    }
}
