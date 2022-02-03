package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.handler.exception.LineException;
import nextstep.subway.handler.exception.StationException;
import nextstep.subway.handler.validator.SectionValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.handler.exception.ErrorCode.LINE_NOT_FOUND_BY_ID;
import static nextstep.subway.handler.exception.ErrorCode.STATION_NOT_FOUND_BY_ID;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Line line = lineRepository.save(Line.of(request.getName(), request.getColor(),
                upStation, downStation, request.getDistance()));

        return createLineResponse(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return createLineResponse(lineRepository.findById(id)
                .orElseThrow(() -> new LineException(LINE_NOT_FOUND_BY_ID)));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new LineException(LINE_NOT_FOUND_BY_ID));

        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new LineException(LINE_NOT_FOUND_BY_ID));

        SectionValidator.validateOnlyOneStationExists(line, upStation, downStation);

        line.addSection(createSection(line, upStation, downStation, sectionRequest.getDistance()));
    }

    private Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return Section.of(line, upStation, downStation, distance);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new LineException(LINE_NOT_FOUND_BY_ID));
        Station station = findStationById(stationId);

        if (!line.getSections().get(line.getSections().size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        line.getSections().remove(line.getSections().size() - 1);
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                createStationResponses(line),
                line.getCreatedDate(),
                line.getModifiedDate()
        );
    }

    private List<StationResponse> createStationResponses(Line line) {
        if (line.getSections().isEmpty()) {
            return Collections.emptyList();
        }

        return line.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationException(STATION_NOT_FOUND_BY_ID));
    }
}
