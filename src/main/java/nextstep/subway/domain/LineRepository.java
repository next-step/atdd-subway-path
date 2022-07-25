package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Override
    @Query("select l from Line l left join fetch l.sections.sections s left join fetch s.downStation left join fetch s.upStation")
    List<Line> findAll();

}

