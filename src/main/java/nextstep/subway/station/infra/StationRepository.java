package nextstep.subway.station.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.station.domain.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
}