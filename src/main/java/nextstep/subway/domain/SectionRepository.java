package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query("select s " +
            "from Section s " +
            "join fetch s.line " +
            "where s.upStation.id in :ids or s.downStation.id in :ids")
    List<Section> findByIdStationIds(@Param("ids") List<Long> stationIds);
}
