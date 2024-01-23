package subway.line.service;

import org.springframework.stereotype.Service;
import subway.line.entity.Line;
import subway.line.exception.LineException;
import subway.line.LineRepository;

import javax.transaction.Transactional;

@Transactional
@Service
public class LineDataService {

    private final LineRepository lineRepository;

    public LineDataService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineException("존재하지 않는 노선입니다."));
    }
}
