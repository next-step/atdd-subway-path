package atdd.serivce.stations;

import atdd.domain.stations.Stations;
import atdd.domain.stations.StationsRepository;
import atdd.web.dto.StationsListResponseDto;
import atdd.web.dto.StationsResponseDto;
import atdd.web.dto.StationsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StationsService {

    private final StationsRepository stationsRepository;

    public Stations create(StationsSaveRequestDto requestDto){
        return stationsRepository.save(requestDto.toEntity());
    }

    public void delete(Long id){
        Stations stations=checkId(id);
        stationsRepository.delete(stations);

    }

    public StationsResponseDto findById(Long id){
        Stations stations=checkId(id);
        return new StationsResponseDto(stations);
    }

    public List<StationsListResponseDto> getList() {
        return stationsRepository.findAll()
                .stream()
                .map(StationsListResponseDto::new)
                .collect(Collectors.toList());
    }

    public Stations checkId(Long id){
        return stationsRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("해당 역이 존재하지 않습니다."));
    }
}
