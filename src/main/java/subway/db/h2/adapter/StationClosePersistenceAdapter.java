package subway.db.h2.adapter;

import subway.db.common.PersistenceAdapter;
import subway.db.h2.repository.StationRepository;
import subway.application.out.StationClosePort;
import subway.domain.Station;

@PersistenceAdapter
public class StationClosePersistenceAdapter implements StationClosePort {
    private final StationRepository stationRepository;

    public StationClosePersistenceAdapter(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public void closeStation(Station.Id id) {
        stationRepository.deleteById(id.getValue());
    }
}
