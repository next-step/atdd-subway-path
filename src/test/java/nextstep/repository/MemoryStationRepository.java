package nextstep.repository;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

import java.util.*;

public class MemoryStationRepository implements StationRepository {
    private final HashMap<Long, Station> stations = new HashMap<>();

    @Override
    public Station save(Station entity) {
        boolean isNew = Objects.isNull(entity.getId());
        if (!isNew) {
            return merge(entity);
        }

        checkDuplicateName(entity);

        long id = stations.size()+1;
        Station newStation = new Station(id, entity.getName());
        stations.put(id, newStation);
        return newStation;
    }

    private void checkDuplicateName(Station entity) {
        stations.values().stream()
                .filter(line->line.getName().equals(entity.getName()))
                .findFirst()
                .ifPresent(line->{throw new RuntimeException();});
    }

    private Station merge(Station entity) {
        Station station = findById(entity.getId()).orElseThrow(IllegalArgumentException::new);
        station.update(entity);
        return station;
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(station -> stations.remove(id, station));
    }

    @Override
    public Optional<Station> findById(Long id) {
        return Optional.ofNullable(stations.get(id));
    }
}
