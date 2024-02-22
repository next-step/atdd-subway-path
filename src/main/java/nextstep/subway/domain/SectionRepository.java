package nextstep.subway.domain;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

  @Override
  @EntityGraph(attributePaths = {"upStation", "downStation"})
  List<Section> findAll();
}
