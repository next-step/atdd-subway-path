package nextstep.subway.station.repository;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
    Station save(Station station);
    Optional<Station> findById(Long id);
    List<Station> findAll();
    void deleteById(Long id);
}
