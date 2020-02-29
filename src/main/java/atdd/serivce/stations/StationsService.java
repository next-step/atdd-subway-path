package atdd.serivce.stations;

import atdd.domain.stations.Stations;
import atdd.domain.stations.StationsRepository;
import atdd.web.dto.StationsListResponseDto;
import atdd.web.dto.StationsResponseDto;
import atdd.web.dto.StationsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StationsService {

    private final StationsRepository stationsRepository;

    public StationsResponseDto create(StationsSaveRequestDto requestDto){
        Stations createdStation=stationsRepository.save(requestDto.toEntity());
        return new StationsResponseDto().toRealDto(createdStation);
    }

    public void delete(Long id){
        Stations stations=checkId(id);
        stationsRepository.delete(stations);
    }


    public StationsListResponseDto readList() {
       List<Stations> stationsList=stationsRepository.findAll();
       StationsListResponseDto dto=new StationsListResponseDto();
       return dto.toDtoEntity(stationsList);
    }

    public StationsResponseDto read(Long id) {
        Stations station =checkId(id);
        StationsResponseDto dto=new StationsResponseDto();
        return dto.toRealDto(station);
    }

    public Stations checkId(Long id){
        return stationsRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("해당 역이 존재하지 않습니다."));
    }
}
