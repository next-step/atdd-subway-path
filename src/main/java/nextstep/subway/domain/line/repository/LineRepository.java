package nextstep.subway.domain.line.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.domain.line.domain.Line;

public interface LineRepository extends JpaRepository<Line, Long> {
    default Line getLineById(Long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("해당 노선을 찾을 수 없습니다."));
    }
}
