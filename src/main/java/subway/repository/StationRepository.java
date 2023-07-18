package subway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import subway.entity.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
}