package subway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import subway.domain.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
}