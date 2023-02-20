package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;

public interface StationRepository {

    Station save(Station station);

    List<Station> findAll();

    void deleteById(Long id);

    List<Station> findAllById(Iterable<Long> ids);

    Optional<Station> findById(Long id);
}
