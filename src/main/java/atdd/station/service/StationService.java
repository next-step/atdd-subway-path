package atdd.station.service;

import atdd.station.domain.station.StationRepository;
import atdd.station.web.dto.StationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StationService {
    private final StationRepository stationRepository;

    @Transactional
    public Long create(StationRequestDto stationRequestDto){
        return stationRepository.save(stationRequestDto.toEntity()).getId();
    }

}
