package nextstep.subway.station.repository;

import nextstep.subway.station.repository.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
}
