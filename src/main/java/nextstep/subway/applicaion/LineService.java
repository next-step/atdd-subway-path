package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import nextstep.subway.handler.exception.LineException;
import nextstep.subway.handler.exception.StationException;
import nextstep.subway.handler.validator.SectionValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /* 노선 생성을 처리한다. */
    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Line line = createLine(request.getName(), request.getColor(), upStation, downStation, request.getDistance());

        return LineResponse.of(line);
    }

    private Line createLine(String name, String color, Station upStation, Station downStation, int distance) {
        return lineRepository.save(Line.of(name, color, upStation, downStation, distance));
    }

    /* 모든 노선의 정보가 담긴 리스트를 반환한다. */
    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return getAllLines();
    }

    private List<LineResponse> getAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    /* 단일 노선의 정보를 반환한다. */
    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.of(findLineById(id));
    }

    /* 노선에 정보 변경을 처리한다. */
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);

        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    /* 노선의 삭제를 처리한다. */
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    /* 노선에 구간 추가를 처리한다. */
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());
        Line line = findLineById(lineId);

        SectionValidator.validateOnlyOneStationExists(line, upStation, downStation);

        line.addSection(createSection(line, upStation, downStation, sectionRequest.getDistance()));
    }

    private Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return Section.of(line, upStation, downStation, distance);
    }

    /* 노선에 구간 삭제를 처리한다. */
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = findStationById(stationId);

        SectionValidator.validateOnlyOneSection(line);

        line.removeSectionByStation(station);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineException(LINE_NOT_FOUND_BY_ID));
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationException(STATION_NOT_FOUND_BY_ID));
    }
}
