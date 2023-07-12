package subway.line.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import subway.line.model.Line;

public interface LineRepository extends JpaRepository<Line, Long> {
}
