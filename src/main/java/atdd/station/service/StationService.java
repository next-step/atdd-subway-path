package atdd.station.service;

import atdd.station.dto.StationResponseDto;
import atdd.station.domain.Station;
import atdd.station.repository.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponseDto create(String name) {
        final Station savedStation = stationRepository.save(new Station(name));
        return StationResponseDto.from(savedStation);
    }


}
