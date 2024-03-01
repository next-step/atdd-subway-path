package nextstep.subway.domain.repository;

import nextstep.subway.domain.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {

}
