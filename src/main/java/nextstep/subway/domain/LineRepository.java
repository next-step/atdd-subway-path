package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Override
    List<Line> findAll();

    @Query("select distinct l " +
            "from Line l " +
            "join fetch l.sections.values s " +
            "where s.upStation.id in :ids or s.downStation.id in :ids")
    Set<Line> findByStationIds(@Param("ids") List<Long> stationIds);
}