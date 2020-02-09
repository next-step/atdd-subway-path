package atdd.station.service;

import atdd.station.model.Station;
import atdd.station.model.StationRequest;
import atdd.station.repository.StationRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Station save(final StationRequest stationRequest) {
        return stationRepository.save(stationRequest.toStation());
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public Station findByName(final String name) {
        return stationRepository.findByName(name);
    }

    @Transactional
    public void deleteStation(final String name) {
        final Station station = findByName(name);
        stationRepository.deleteById(station.getId());
    }
}
