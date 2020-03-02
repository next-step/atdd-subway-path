package atdd.path.repository;

import atdd.path.domain.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeRepository extends JpaRepository<Edge, Long> {
}
