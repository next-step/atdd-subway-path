package nextstep.subway.line.delete;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;

@Transactional
@Service
public class LineDeleteService {

    private final LineRepository lineRepository;

    public LineDeleteService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public void deleteLine(Long lineId) {
        Line line = findLineByLineId(lineId);
        lineRepository.delete(line);
    }

    private Line findLineByLineId(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다. lineId: " + lineId));
    }
}
