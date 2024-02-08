package nextstep.subway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.domain.Line;

public interface LineRepository extends JpaRepository<Line, Long> {
}
