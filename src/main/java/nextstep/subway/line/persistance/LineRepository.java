package nextstep.subway.line.persistance;

import nextstep.subway.line.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
}
