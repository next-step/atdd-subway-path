package atdd.station.repository;


import atdd.station.entity.StationEntity;
import atdd.station.entity.StationListEntity;
import org.springframework.data.repository.Repository;

public interface StationRepository extends Repository<StationEntity, Long> {
  StationEntity save(StationEntity stationEntity);
  StationListEntity getAllStation();
  StationEntity getByName(String name);
  void removeByName(String name);
}
