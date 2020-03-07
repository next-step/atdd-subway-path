package atdd.station.service;

import atdd.global.exception.ServiceNotFoundException;
import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import atdd.station.domain.query.StationQueryView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository stationRepository;

    @Transactional
    public Station save(Station station) {
        return stationRepository.save(station);
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public Optional<Station> findStationById(Long id) {
        return stationRepository.findById(id);
    }

    public StationQueryView findStationWithLine(Long id) {
        return stationRepository.findStationWithLine(id);
    }

    @Transactional
    public void deleteStationById(Long id) {
        final Station findStation = findStationById(id)
                .orElseThrow(() -> new ServiceNotFoundException("지하철 역이 존재하지 않습니다.", Map.of("id", id)));

        stationRepository.deleteById(findStation.getId());
    }

}
