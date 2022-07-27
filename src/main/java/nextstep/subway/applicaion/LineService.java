package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSection(upStation, downStation, request.getDistance());
        }
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                             .map(LineResponse::of)
                             .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.of(findLine(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLine(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findLine(final Long id) {
        return lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
