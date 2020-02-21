package atdd.line.repository;

import atdd.line.domain.LineStation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {

}
