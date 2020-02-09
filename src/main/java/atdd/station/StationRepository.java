package atdd.station;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

	@Query("select s from Station s where s.name = :name")
	Optional<Station> findByName(@Param("name") String name);

	@Transactional
	@Modifying
	void deleteByName(String name);
}
