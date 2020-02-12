package atdd.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    @Override
    public Station create(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public void delete(Station station) {
        stationRepository.delete(station);
    }

    @Override
    public List<Station> findAll() {
        return stationRepository.findAll();
    }
}
