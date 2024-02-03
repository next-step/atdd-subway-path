package nextstep.subway.line;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Query("SELECT distinct l FROM Line l join fetch l.sections")
    public List<Line> findAllFetchJoin();

    @Query("SELECT distinct l FROM Line l join fetch l.sections where l.id = :id")
    public Optional<Line> findByIdFetchJoin(Long id);
}
