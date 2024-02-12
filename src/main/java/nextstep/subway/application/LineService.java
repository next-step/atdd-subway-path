package nextstep.subway.application;

import nextstep.subway.application.dto.LineRequest;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    public static final String EMPTY_LINE_MSG = "존재하지 않는 노선 입니다.";
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest lineRequest) {
        final Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        final Station downStation = stationService.findStationById(lineRequest.getDownStationId());

        final Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance());
        final Line savedLine = lineRepository.save(line);

        return new LineResponse(savedLine);
    }

    public List<LineResponse> findAllLineResponses() {
        return lineRepository.findAllFetchJoin().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public List<Line> findAllLine() {
        return new ArrayList<>(lineRepository.findAllFetchJoin());
    }

    public LineResponse findLineById(final Long id) {
        final Line line = lineRepository.findByIdFetchJoin(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, EMPTY_LINE_MSG));

        return new LineResponse(line);
    }

    @Transactional
    public void modifyLine(final LineRequest lineRequest) {
        final Line line = lineRepository.findById(lineRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, EMPTY_LINE_MSG));

        line.changeName(lineRequest.getName());
        line.changeColor(lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(final Long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, EMPTY_LINE_MSG));

        lineRepository.delete(line);
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, EMPTY_LINE_MSG));

        final Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
        final Station downStation = stationService.findStationById(sectionRequest.getDownStationId());

        line.addSection(upStation, downStation, sectionRequest.getDistance());
    }

    @Transactional
    public void deleteSection(final Long stationId, final Long lineId) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, EMPTY_LINE_MSG));

        final Station deleteStation = stationService.findStationById(stationId);

        line.removeSection(deleteStation);
    }
}
