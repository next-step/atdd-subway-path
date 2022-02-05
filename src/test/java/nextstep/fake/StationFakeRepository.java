package nextstep.fake;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class StationFakeRepository implements StationRepository {

    private Map<Long, Station> stations = new ConcurrentHashMap<>();

    @Override
    public List<Station> findAll() {
        return null;
    }

    @Override
    public Station save(Station entity) {
        Station station = new Station(Long.valueOf(stations.size()), entity.getName());
        stations.put(Long.valueOf(stations.size()), station);
        return station;
    }

    @Override
    public void deleteById(Long id) {

    }

    public Optional<Station> findById(Long aLong) {
        return Optional.of(stations.get(aLong));
    }

    @Override
    public Optional<Station> findByName(String name) {
        return Optional.empty();
    }
}
