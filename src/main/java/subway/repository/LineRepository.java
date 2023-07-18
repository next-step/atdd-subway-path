package subway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import subway.entity.Line;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {
}
