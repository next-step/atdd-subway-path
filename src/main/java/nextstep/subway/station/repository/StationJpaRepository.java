package nextstep.subway.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StationJpaRepository extends JpaRepository<Station, Long>, StationRepository {
}
