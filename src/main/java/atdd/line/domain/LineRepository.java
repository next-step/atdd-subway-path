package atdd.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select distinct l from Line l " +
            "left join fetch l.edges e " +
            "left join fetch e.sourceStation " +
            "left join fetch e.targetStation " +
            "where l.id = :id")
    Optional<Line> findLineWithEdgeById(@Param("id") Long id);

    @Query("select distinct l from Line l " +
            "left join fetch l.edges e " +
            "left join fetch e.sourceStation " +
            "left join fetch e.targetStation ")
    List<Line> findLineWithEdgeAll();

}
