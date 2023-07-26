package nextstep.subway.line.repository;

import java.util.List;
import java.util.Optional;

public interface LineRepository {
    Line save(Line line);
    List<Line> findAll();
    Optional<Line> findById(Long id);
    void deleteById(Long id);

}
