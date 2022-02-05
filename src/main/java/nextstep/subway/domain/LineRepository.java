package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;

public interface LineRepository {
    Optional<Line> findByName(String name);

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void deleteById(Long id);
}
