package atdd.station.service;

import atdd.station.domain.station.Station;
import atdd.station.domain.station.StationRepository;
import atdd.station.web.dto.StationRequestDto;
import atdd.station.web.dto.StationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class StationService {
    private final StationRepository stationRepository;

    public Long create(StationRequestDto stationRequestDto){
        return stationRepository.save(stationRequestDto.toEntity()).getId();
    }

    public List<Station> select(){
        return stationRepository.findAll();
    }

    public StationResponseDto findById(Long id){
        Station entity = stationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        return new StationResponseDto(entity);
    }

    public void delete(Long id) {
        Station station = stationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        stationRepository.delete(station);
    }

}
