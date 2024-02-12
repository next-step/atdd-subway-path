package nextstep.subway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nextstep.subway.domain.Line;
@Repository
public interface LineRepository extends JpaRepository<Line, Long> {
}
