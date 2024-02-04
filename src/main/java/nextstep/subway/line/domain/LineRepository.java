package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/** 지하철 노선 관리 리포지토리 */
public interface LineRepository extends JpaRepository<Line, Long> {
}
