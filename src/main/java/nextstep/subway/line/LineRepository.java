package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    List<Line> findAll();

    Optional<Line> findById(Long lineId);
}