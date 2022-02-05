package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLineRepository extends LineRepository, JpaRepository<Line,Long> {
}
