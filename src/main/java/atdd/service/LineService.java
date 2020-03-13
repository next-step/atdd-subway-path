package atdd.service;

import atdd.domain.Line;
import atdd.domain.repository.LineRepository;
import org.springframework.stereotype.Service;

@Service
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line create(Line line) {
        return null;
    }
}
