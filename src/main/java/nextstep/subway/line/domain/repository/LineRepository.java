package nextstep.subway.line.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.line.domain.model.Line;

public interface LineRepository extends JpaRepository<Line, Long> {
}
