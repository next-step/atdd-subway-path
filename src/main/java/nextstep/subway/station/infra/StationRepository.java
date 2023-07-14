package nextstep.subway.station.infra;

import java.util.List;
import java.util.Optional;
import nextstep.subway.station.domain.Station;

public interface StationRepository {
  Station save(Station station);
  List<Station> findAll();
  Optional<Station> findById(Long id);
  void deleteById(Long id);
}