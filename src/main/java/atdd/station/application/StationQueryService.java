package atdd.station.application;

import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class StationQueryService {

    private StationRepository stationRepository;

    public StationQueryService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public List<Station> getStations() {
        return stationRepository.findAll();
    }

}
