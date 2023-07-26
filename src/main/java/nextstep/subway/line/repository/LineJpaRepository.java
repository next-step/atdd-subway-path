package nextstep.subway.line.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineJpaRepository extends JpaRepository<Line, Long>, LineRepository {
}
