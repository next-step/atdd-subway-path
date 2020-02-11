package atdd.line.service;

import atdd.line.domain.Line;
import atdd.line.domain.LineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LineService {

    private final LineRepository lineRepository;

    @Transactional
    public Line save(Line line) {
        return lineRepository.save(line);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Optional<Line> findLineById(Long id) {
        return lineRepository.findById(id);
    }

}
