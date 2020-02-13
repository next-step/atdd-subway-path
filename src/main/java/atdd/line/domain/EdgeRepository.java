package atdd.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, Long> {

    @Query("select  e from Edge e join fetch e.targetStation join fetch e.sourceStation " +
            "where e.line.id = :lineId order by e.id desc")
    List<Edge> findEdgesByLineId(@Param("lineId") Long lineId);

    @Query("select e from Edge e join fetch e.line " +
            "where e.sourceStation.id = :stationId or e.targetStation.id = :stationId")
    List<Edge> findEdgesByStationId(@Param("stationId") Long stationId);

}
