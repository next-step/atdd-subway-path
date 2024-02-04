package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.line.domain.Line;

public interface LineRepository extends JpaRepository<Line, Long> {
}
