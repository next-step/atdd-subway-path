package atdd.station.service;

import antlr.debug.TraceEvent;
import atdd.station.domain.station.Station;
import atdd.station.domain.station.StationRepository;
import atdd.station.web.dto.StationRequestDto;
import atdd.station.web.dto.StationResponseDto;
import lombok.RequiredArgsConstructor;
import org.omg.CORBA.TRANSACTION_MODE;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class StationService {
    private final StationRepository stationRepository;

    public Station create(StationRequestDto stationRequestDto){
        return stationRepository.save(stationRequestDto.toEntity());
    }

    public List<StationResponseDto> select(){
        List<StationResponseDto> stationResponseDtoList = stationRepository.findAll()
                .stream()
                .map(StationResponseDto::new)
                .collect(Collectors.toList());
        return stationResponseDtoList;
    }

    public StationResponseDto findById(Long id){
        Station entity = stationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 지하철역이 없습니다."));
        return new StationResponseDto(entity);
    }

    public void delete(Long id) {
        Station station = stationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 지하철역이 없습니다."));
        stationRepository.delete(station);
    }

}
