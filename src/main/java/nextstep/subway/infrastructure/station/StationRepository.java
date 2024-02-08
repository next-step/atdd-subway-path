package nextstep.subway.infrastructure.station;

import nextstep.subway.domain.station.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
}