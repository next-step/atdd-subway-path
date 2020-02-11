package atdd.line.service;

import atdd.line.domain.Line;
import atdd.line.domain.LineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LineService {

    private final LineRepository lineRepository;

    @Transactional
    public Line save(Line line) {
        return lineRepository.save(line);
    }

}
