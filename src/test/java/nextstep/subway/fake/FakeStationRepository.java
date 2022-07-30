package nextstep.subway.fake;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeStationRepository implements StationRepository {

    private static final Map<Long, Station> stationMap = new HashMap<>();
    private static long sequence = 1L;


    @Override
    public Optional<Station> findById(Long aLong) {
        return Optional.ofNullable(stationMap.get(aLong));
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stationMap.values());
    }

    @Override
    public void deleteById(Long aLong) {
        stationMap.remove(aLong);
    }

    @Override
    public Station save(Station entity) {
        long id = ++sequence;
        Station station = new Station(id, entity.getName());
        stationMap.put(id, station);
        return station;
    }
}
