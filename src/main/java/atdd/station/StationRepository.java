package atdd.station;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StationRepository extends JpaRepository<Station, Long> {

	@Query("select s from Station s where s.name = :name")
	Station findByName(@Param("name") String name);
}
