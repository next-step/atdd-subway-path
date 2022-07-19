package nextstep.subway.fake;

import nextstep.subway.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FakeLineRepository extends JpaRepository<Line, Long> {
    @Override
    List<Line> findAll();
}
