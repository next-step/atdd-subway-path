package atdd.path.service;

import atdd.path.application.dto.CreateStationRequestView;
import atdd.path.application.dto.StationResponseView;
import atdd.path.domain.Station;
import atdd.path.domain.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponseView create(CreateStationRequestView requestView) {
        Station savedStation = stationRepository.save(requestView.toStation());
        return StationResponseView.of(savedStation);
    }
}
