package nextstep.subway.application;

import nextstep.subway.application.dto.LineRequest;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    public static final String EMPTY_LINE_MSG = "존재하지 않는 노선 입니다.";
    public static final String EMPTY_UP_STATION_MSG = "존재 하지 않는 상행종점역 입니다.";
    public static final String EMPTY_DOWN_STATION_MSG = "존재 하지 않는 하행종점역 입니다.";
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest lineRequest) {
        final Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException(EMPTY_UP_STATION_MSG));
        final Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException(EMPTY_DOWN_STATION_MSG));

        final Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance());
        final Line savedLine = lineRepository.save(line);

        return new LineResponse(savedLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAllFetchJoin().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
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
}
