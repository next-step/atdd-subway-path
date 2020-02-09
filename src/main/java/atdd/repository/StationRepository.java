package atdd.repository;

import atdd.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StationRepository extends JpaRepository<Station, Long> {
    @Query("SELECT t FROM Station t WHERE t.name = ?1")
    Station findByStationName(String stationName);
}
