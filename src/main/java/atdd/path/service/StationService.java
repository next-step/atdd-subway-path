package atdd.path.service;

import atdd.path.application.dto.CreateStationRequestView;
import atdd.path.application.dto.StationResponseView;
import atdd.path.domain.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class StationService {
    private StationRepository repository;

    public StationService(StationRepository repository) {
        this.repository = repository;
    }

    public StationResponseView create(CreateStationRequestView requestView) {

        return new StationResponseView();
    }
}
