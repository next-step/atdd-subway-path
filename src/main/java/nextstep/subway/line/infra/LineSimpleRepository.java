package nextstep.subway.line.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.line.domain.Line;

public interface LineSimpleRepository extends LineRepository, JpaRepository<Line, Long> {

}
