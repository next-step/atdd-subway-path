package nextstep.subway.station.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.station.domain.Station;

public interface StationSimpleRepository extends StationRepository, JpaRepository<Station, Long> {
}