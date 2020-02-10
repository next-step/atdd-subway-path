package atdd.station.repository;

import atdd.station.entity.StationEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@org.springframework.stereotype.Repository
public interface StationRepository extends Repository<StationEntity, Long> {
  Optional<StationEntity> save(StationEntity stationEntity);
  List<StationEntity> findAll();
  Optional<StationEntity> getByname(String name);
  Optional<StationEntity> getByid(Long id);
  void removeByid(Long id);
}
