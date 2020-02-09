package atdd.serivce.stations;

import atdd.domain.stations.Stations;
import atdd.domain.stations.StationsRepository;
import atdd.web.dto.StationsResponseDto;
import atdd.web.dto.StationsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StationsService {

    private final StationsRepository stationsRepository;

    public Stations create(StationsSaveRequestDto requestDto){
        return stationsRepository.save(requestDto.toEntity());
    }

    public void delete(Long id){
        Stations stations=stationsRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 역이 없습니다. id="+id));
        stationsRepository.delete(stations);

    }

    public StationsResponseDto findById(Long id){
        Stations entity=stationsRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 역이 없습니다. id="+id));
        return new StationsResponseDto(entity);
    }
}
