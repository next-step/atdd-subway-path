package atdd.station.application;

import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StationCommandService {
    private StationQueryService stationQueryService;
    private StationRepository stationRepository;

    public StationCommandService(StationQueryService stationQueryService,
                                 StationRepository stationRepository) {

        this.stationQueryService = stationQueryService;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Station create(String stationName) {
        return stationRepository.save(Station.of(stationName));
    }

    @Transactional
    public void deleteStation(Long id) {
        stationRepository.delete(stationQueryService.getStation(id));
    }
}
