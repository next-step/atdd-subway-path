package atdd.station.application;

import atdd.station.application.exception.ResourceNotFoundException;
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

    @Transactional
    public Station getStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 지하철역을 찾을 수 없습니다."));
    }
}
