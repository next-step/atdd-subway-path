package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Query("SELECT l FROM Line l left join fetch l.sections.sections")
    List<Line> findAll();
}
