package atdd.Edge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, Long> {
    List<Edge> findByLineId(Long lineId);
    @Query(value = "select * from edge where SOURCE_STATION_ID =:stationId or TARGET_STATION_ID=:stationId", nativeQuery = true)
    List<Edge> findByStationId(@Param("stationId") Long stationId);
}
