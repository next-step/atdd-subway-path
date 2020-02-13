package atdd.station.repository;

import atdd.station.entity.Station;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface StationRepository extends Repository<Station, Long> {
  Optional<Station> save(Station station);
  List<Station> findAll();
  Optional<Station> getByname(String name);
  Optional<Station> getByid(Long id);
  void removeByid(Long id);
}
