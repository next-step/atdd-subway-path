package nextstep.subway.applicaion.line;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.line.request.LineCreateRequest;
import nextstep.subway.applicaion.line.request.LineUpdateRequest;
import nextstep.subway.applicaion.line.response.LineResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.StationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse saveLine(final LineCreateRequest request) {
        final var line = lineRepository.save(convertToLine(request));
        return LineResponse.toResponse(line);
    }

    private Line convertToLine(final LineCreateRequest request) {
        final var upStation = stationRepository.getById(request.getUpStationId());
        final var downStation = stationRepository.getById(request.getDownStationId());

        return new Line(
                request.getName(),
                request.getColor(),
                upStation,
                downStation,
                request.getDistance()
        );
    }

    @Transactional
    public void updateLine(final Long id, final LineUpdateRequest request) {
        final var line = lineRepository.getById(id);
        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(final Long id) {
        final var line = lineRepository.getById(id);
        lineRepository.delete(line);
    }

    public List<LineResponse> findAllLines() {
        final var lines = lineRepository.findAll();
        return LineResponse.toResponses(lines);
    }

    public LineResponse findLine(final Long id) {
        final var line = lineRepository.getById(id);
        return LineResponse.toResponse(line);
    }
}
