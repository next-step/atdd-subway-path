package subway.db.h2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import subway.db.h2.entity.StationJpa;

import java.util.List;

public interface StationRepository extends JpaRepository<StationJpa, Long> {

    List<StationJpa> findAllByIdIn(List<Long> stationIds);
}