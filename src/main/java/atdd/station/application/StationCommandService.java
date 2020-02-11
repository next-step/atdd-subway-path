package atdd.station.application;

import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
public class StationCommandService {
    private StationRepository stationRepository;

    public StationCommandService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Station create(String stationName) {
        return stationRepository.save(Station.of(stationName));
    }

    @Transactional
    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
    }
}
