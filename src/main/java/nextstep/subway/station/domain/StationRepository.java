package nextstep.subway.station.domain;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
    Station save(Station toStation);

    List<Station> findAll();

    void deleteById(Long id);

    Optional<Station> findById(Long id);
}
