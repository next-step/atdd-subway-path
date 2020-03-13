package atdd.service;

import atdd.domain.Station;
import atdd.domain.repository.StationRepository;
import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
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

    public void delete(long id) {
        stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("등록된 지하철역만 삭제 가능합니다."));
        stationRepository.deleteById(id);
    }

    public Station findById(long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("등록된 지하철역만 조회 가능합니다."));
        return station;
    }
}
