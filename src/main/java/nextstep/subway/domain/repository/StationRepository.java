package nextstep.subway.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.domain.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
}