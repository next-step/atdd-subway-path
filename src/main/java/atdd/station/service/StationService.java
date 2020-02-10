package atdd.station.service;

import atdd.station.dao.StationRepository;
import atdd.station.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station create(final Station station) {
        return stationRepository.save(station);
    }
}
