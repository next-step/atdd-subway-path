package atdd.station.repository;


import atdd.station.entity.StationEntity;
import org.springframework.data.repository.Repository;

public interface StationRepository extends Repository<StationEntity, Long> {
  StationEntity save(StationEntity stationEntity);
}
