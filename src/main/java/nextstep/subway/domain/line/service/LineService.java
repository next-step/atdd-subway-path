package nextstep.subway.domain.line.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.domain.line.domain.Line;
import nextstep.subway.domain.line.dto.request.CreateLineRequest;
import nextstep.subway.domain.line.dto.request.UpdateLineRequest;
import nextstep.subway.domain.line.dto.response.LineResponse;
import nextstep.subway.domain.line.repository.LineRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;

    @Transactional
    public LineResponse creteLine(CreateLineRequest request) {
        Line line = Line.create(
                request.getName(),
                request.getColor()
        );

        Line savedLine = lineRepository.save(line);
        return LineResponse.from(savedLine);
    }

    public List<LineResponse> getLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse getLineById(Long lineId) {
        Line line = lineRepository.getLineById(lineId);
        return LineResponse.from(line);
    }

    @Transactional
    public LineResponse updateLine(Long lineId, UpdateLineRequest request) {
        Line line = lineRepository.getLineById(lineId);
        line.update(request.getName(), request.getColor());
        return LineResponse.from(line);
    }

    @Transactional
    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }
}
