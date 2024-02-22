package nextstep.subway.line.load;

import org.springframework.stereotype.Service;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineLoadService {

    private final LineRepository lineRepository;

    public LineLoadService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<LineLoadedResponse> loadLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineLoadedResponse::from)
                .collect(Collectors.toList());
        }

    public LineLoadedResponse loadLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다. lineId: " + lineId));
        return LineLoadedResponse.from(line);
    }
}
