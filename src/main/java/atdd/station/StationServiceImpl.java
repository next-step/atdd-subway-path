package atdd.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    @Override
    @Transactional
    public Station create(Station station) {
        return stationRepository.save(station);
    }

    @Override
    @Transactional
    public void delete(Station station) {
        stationRepository.delete(station);
    }
}
