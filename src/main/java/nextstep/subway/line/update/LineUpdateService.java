package nextstep.subway.line.update;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;

@Transactional
@Service
public class LineUpdateService {

    private final LineRepository lineRepository;

    public LineUpdateService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public void updateLine(Long lineId, LineUpdateRequest request) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다. lineId: " + lineId));

        line.update(request.getName(), request.getColor());
    }
}
