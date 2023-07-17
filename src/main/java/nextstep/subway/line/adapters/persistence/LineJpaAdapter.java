package nextstep.subway.line.adapters.persistence;

import lombok.RequiredArgsConstructor;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.NotEntityFoundException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineJpaAdapter {

    private final LineRepository lineRepository;

    @Transactional
    public Line save(Line line) {
        return lineRepository.save(line);
    }

    public Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotEntityFoundException(ErrorCode.NOT_EXIST_LINE));
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }
}
