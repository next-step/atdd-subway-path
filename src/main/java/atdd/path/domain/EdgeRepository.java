package atdd.path.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EdgeRepository extends JpaRepository<Edge, Long> {
    @Query(value = "SELECT * FROM edge WHERE line_id=?", nativeQuery = true)
    List<Long> findAllStationIdByLineId(Long id);
}
