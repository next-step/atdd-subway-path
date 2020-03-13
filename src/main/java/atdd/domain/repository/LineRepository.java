package atdd.domain.repository;

import atdd.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
}
