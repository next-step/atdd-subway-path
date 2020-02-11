package atdd.line.service;

import atdd.global.exception.ServiceNotFoundException;
import atdd.line.domain.Line;
import atdd.line.domain.LineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    public Line findLineById(Long id) {
        return findById(id);
    }

    @Transactional
    public void deleteLineById(Long id) {
        final Line findLine = findById(id);
        lineRepository.deleteById(findLine.getId());
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("지하철 노선이 존재하지 않습니다.", Map.of("id", id)));
    }

}
