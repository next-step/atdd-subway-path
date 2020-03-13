package atdd.service;

import atdd.domain.Station;
import atdd.domain.repository.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station create(Station station) {
        Station savedStation = stationRepository.save(station);
        return savedStation;
    }
}
