package nextstep.subway.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

public class InMemoryStationRepository implements StationRepository {

    private Map<Long, Station> stations = new HashMap<>();

    @Override
    public List<Station> findAllById(Iterable<Long> ids) {
        final List<Long> idList = StreamSupport.stream(ids.spliterator(), false)
            .collect(Collectors.toList());

        return stations.entrySet().stream()
            .filter(it -> idList.contains(it.getKey()))
            .map(it -> it.getValue())
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Station> findById(Long id) {
        return Optional.ofNullable(stations.get(id));
    }

    @Override
    public Station save(Station station) {
        stations.put(station.getId(), station);
        return station;
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    @Override
    public void deleteById(Long id) {
        stations.remove(id);
    }
}
