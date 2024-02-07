package nextstep.subway.api.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.api.domain.model.entity.Line;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
public interface LineRepository extends JpaRepository<Line, Long> {
}
