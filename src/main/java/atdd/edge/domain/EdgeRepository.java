package atdd.edge.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, Long> {

	List<Edge> findAllByLine(Long id);
}
