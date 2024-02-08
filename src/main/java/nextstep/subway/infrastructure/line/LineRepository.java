package nextstep.subway.infrastructure.line;

import nextstep.subway.domain.line.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository  extends JpaRepository<Line, Long> {
}
