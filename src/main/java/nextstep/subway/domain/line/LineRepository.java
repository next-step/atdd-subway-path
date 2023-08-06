package nextstep.subway.domain.line;

import nextstep.subway.domain.line.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Override
    List<Line> findAll();
}