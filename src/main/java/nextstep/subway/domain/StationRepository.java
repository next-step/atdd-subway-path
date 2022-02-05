package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
    List<Station> findAll();

    Optional<Station> findByName(String name);

    Station save(Station station);

    void deleteById(Long id);

    Optional<Station> findById(Long upStationId);
}
