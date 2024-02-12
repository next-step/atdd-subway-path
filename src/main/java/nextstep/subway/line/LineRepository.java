package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Query("SELECT distinct l FROM Line l join fetch l.sections.sectionList section join fetch section.upStation join fetch section.downStation")
    public List<Line> findAllFetchJoin();
}
