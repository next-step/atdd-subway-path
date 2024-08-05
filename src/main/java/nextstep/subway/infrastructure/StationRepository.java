package nextstep.subway.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.domain.Station;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByIdIn(List<Long> ids);
}
