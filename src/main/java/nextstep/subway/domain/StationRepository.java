package nextstep.subway.domain;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {

  List<Station> findAllByIdIn(Collection<Long> ids);
}