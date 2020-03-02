package atdd.path.service;

import atdd.path.application.dto.StationRequestView;
import atdd.path.application.dto.StationResponseView;
import atdd.path.domain.Station;
import atdd.path.domain.StationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<Station> station = stationRepository.findById(requestView.getId());
        if(Optional.empty().isPresent()){
            throw new RuntimeException("존재하지 않는 지하철역은은 삭제할 수 없습니다.");
        }
        stationRepository.deleteById(requestView.getId());
    }
}
