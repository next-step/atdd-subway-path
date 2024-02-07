package nextstep.subway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.entity.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
}