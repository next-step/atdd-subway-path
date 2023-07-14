package nextstep.subway.unit.fake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.infra.StationRepository;

public class StationMemoryRepository implements StationRepository {

  private final Map<Long, Station> data = new HashMap<>();

  @Override
  public Station save(Station station) {
    Long id = (long) (data.size() + 1);
    station = new Station(id, station.getName());
    data.put(id, station);
    return station;
  }

  @Override
  public List<Station> findAll() {
    return (List<Station>) data.values();
  }

  @Override
  public Optional<Station> findById(Long id) {
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public void deleteById(Long id) {
    data.remove(id);
  }
}
