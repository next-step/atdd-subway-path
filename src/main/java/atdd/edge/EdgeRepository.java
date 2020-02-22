package atdd.edge;

import atdd.line.Line;
import atdd.station.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface EdgeRepository extends JpaRepository<Edge, Long> {

    List<Edge> findByLine(Line line);

    @Query("SELECT e from Edge e where e.sourceStation = :station or e.targetStation = :station")
    Set<Edge> findLinesByStation(@Param("station") Station station);
}
