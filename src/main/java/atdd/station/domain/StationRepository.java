package atdd.station.domain;

import atdd.station.domain.query.StationRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long>, StationRepositoryQuery {
}
