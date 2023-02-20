package nextstep.subway.infra;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLineRepository extends LineRepository, JpaRepository<Line, Long> {
}
