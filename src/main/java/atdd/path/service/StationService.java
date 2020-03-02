package atdd.path.service;

import atdd.path.application.dto.StationRequestView;
import atdd.path.application.dto.StationResponseView;
import atdd.path.domain.Station;
import atdd.path.domain.StationRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
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

    public void delete(StationRequestView requestView) throws Exception{
        Optional<Station> station = stationRepository.findById(requestView.getId());
        if(station.isPresent()){
            stationRepository.deleteById(requestView.getId());
        }
    }

    public List<Station> showAll() {
        List<Station> stations = stationRepository.findAll();
        if(stations.size()== 0){
            return Collections.emptyList();
        }
        return stationRepository.findAll();
    }
}
