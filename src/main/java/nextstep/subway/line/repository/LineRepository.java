package nextstep.subway.line.repository;

import nextstep.subway.line.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
}
