package nextstep.subway.line.infra;

import java.util.List;
import java.util.Optional;
import nextstep.subway.line.domain.Line;

public interface LineRepository {
    Line save(Line line);
    List<Line> findAll();
    Optional<Line> findById(Long id);
    void deleteById(Long id);
}
