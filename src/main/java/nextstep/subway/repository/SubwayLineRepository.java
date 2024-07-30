package nextstep.subway.repository;

import nextstep.subway.domain.SubwayLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubwayLineRepository extends JpaRepository<SubwayLine, Long> {
}
