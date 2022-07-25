package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LineQueryService {

    private static final String NO_SUCH_LINE_FORMAT = "요청한 노선은 존재하지 않는 노선입니다. (요청한 id: %d)";

    private final LineRepository lineRepository;

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        return LineResponse.from(findById(lineId));
    }

    public Line findById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(NO_SUCH_LINE_FORMAT, lineId)
                ));
    }

}
