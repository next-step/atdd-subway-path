package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Override
    List<Line> findAll();
}
