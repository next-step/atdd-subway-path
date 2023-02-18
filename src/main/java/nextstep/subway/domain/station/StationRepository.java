package nextstep.subway.domain.station;

import nextstep.subway.domain.station.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
}