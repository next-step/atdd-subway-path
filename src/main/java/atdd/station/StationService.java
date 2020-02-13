package atdd.station;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;


@AllArgsConstructor
@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationResponse findById(Long id){
        Station station = stationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다 {" + id + "}"));
        return StationResponse.of(station);
    }

}
