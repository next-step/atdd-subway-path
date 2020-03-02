package atdd.path.service;

import atdd.path.application.dto.StationRequestView;
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

    public StationResponseView create(StationRequestView requestView) {
        Station savedStation = stationRepository.save(requestView.toStation());
        return StationResponseView.of(savedStation);
    }

    public void delete(StationRequestView requestView) {

    }
}
