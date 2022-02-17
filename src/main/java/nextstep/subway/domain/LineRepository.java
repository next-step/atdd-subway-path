package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
  Optional<Line> findByName(String name);

  @Query("select distinct l from Line l " +
    "join fetch l.sections ss " +
    "join fetch ss.sections s " +
    "join fetch s.upStation up " +
    "join fetch s.downStation down")
  List<Line> findAllWithSections();
}
