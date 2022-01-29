package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineQueryService {
    private final LineRepository lineRepository;

    public LineQueryService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return LineResponse.createLineResponse(lineRepository.findById(id).orElseThrow(IllegalArgumentException::new));
    }

}
