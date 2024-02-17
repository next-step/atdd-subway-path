package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("SELECT distinct l FROM Line l LEFT JOIN FETCH l.sections")
    List<Line> findAllWithSectionsUsingFetchJoin();

    @Query("SELECT distinct l FROM Line l LEFT JOIN FETCH l.sections WHERE l.id = :id")
    Optional<Line> findByIdWithSectionsUsingFetchJoin(@Param(value = "id") Long id);
}