package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;

public interface LineRepository {
    <S extends Line> S save(S entity);

    List<Line> findAll();

    Optional<Line> findById(Long aLong);

    void deleteById(Long aLong);
}
