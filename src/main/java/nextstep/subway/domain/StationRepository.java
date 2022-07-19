package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
    Optional<Station> findById(Long aLong);

    List<Station> findAll();

    void deleteById(Long aLong);

    <S extends Station> S save(S entity);
}
