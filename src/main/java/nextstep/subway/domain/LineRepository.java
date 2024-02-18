package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LineRepository extends JpaRepository<Line, Long> {

    @Override
    @EntityGraph(attributePaths = {"sections"})
    List<Line> findAll();

    @Override
    @EntityGraph(attributePaths = {"sections"})
    Optional<Line> findById(Long id);
}