package nextstep.subway.line.repository;

import nextstep.subway.line.repository.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Query("select line from Line line left join fetch line.sections.sections section join fetch section.upStation join fetch section.downStation")
    List<Line> findAllWithSections();

    @Query("select line from Line line left join fetch line.sections.sections section join fetch section.upStation join fetch section.downStation where line.id = :id")
    Optional<Line> findByIdWithSections(@Param("id") Long id);
}
