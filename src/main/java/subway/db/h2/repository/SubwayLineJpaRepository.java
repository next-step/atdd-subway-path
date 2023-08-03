package subway.db.h2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import subway.db.h2.entity.SubwayLineJpa;

import java.util.List;
import java.util.Optional;

public interface SubwayLineJpaRepository extends JpaRepository<SubwayLineJpa, Long> {

    @Query("SELECT DISTINCT s FROM SubwayLineJpa s JOIN FETCH s.subwaySections")
    List<SubwayLineJpa> findAllWithSections();

    @Query("SELECT DISTINCT s FROM SubwayLineJpa s JOIN FETCH s.subwaySections " +
            "WHERE s.id = :id")
    Optional<SubwayLineJpa> findOneWithSectionsById(@Param("id") Long id);
}
