package atdd.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EdgeRepository extends JpaRepository<Edge, Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from Edge e where e.line.id = :lineId and (e.sourceStation.id = :stationId or e.targetStation.id = :stationId)")
    void deleteEdgeByLineIdAndStationId(@Param("lineId") Long lineId, @Param("stationId") Long stationId);

}
