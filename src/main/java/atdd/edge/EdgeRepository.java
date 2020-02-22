package atdd.edge;

import atdd.line.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, Long> {

    List<Edge> findByLine(Line line);
}
