package nextstep.subway.line.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nextstep.subway.line.domain.model.Line;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {
    @Query("SELECT line FROM Line line"
        + " LEFT JOIN FETCH line.sections.values sections"
        + " LEFT JOIN FETCH sections.upStation"
        + " LEFT JOIN FETCH sections.downStation"
        + " WHERE line.id = :id")
    Optional<Line> findByIdWithStations(long id);

    boolean existsByName(String name);
}
