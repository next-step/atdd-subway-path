package nextstep.subway.applicaion.query;

import nextstep.subway.applicaion.dto.LineAndSectionResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.exception.line.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class LineQueryService {

    private final LineRepository lineRepository;

    public LineQueryService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<LineAndSectionResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(this::createShowLineResponse)
                .collect(toList());
    }

    public LineAndSectionResponse findLine(long id) {
        Line line = findLineById(id);

        return createShowLineResponse(line);
    }

    public Line findLineById(long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException());
    }

    public LineAndSectionResponse createShowLineResponse(Line line) {
        return LineAndSectionResponse.of(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getCreatedDate(),
                line.getModifiedDate(),
                line.getStations()
        );
    }

}
