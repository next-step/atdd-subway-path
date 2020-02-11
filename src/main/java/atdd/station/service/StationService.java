package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import atdd.station.dto.station.StationCreateRequestDto;
import atdd.station.dto.station.StationCreateResponseDto;
import atdd.station.dto.station.StationDetailResponseDto;
import atdd.station.dto.station.StationListResponseDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service("stationService")
public class StationService {
    @Resource(name = "stationRepository")
    private StationRepository stationRepository;

    public StationCreateResponseDto create(StationCreateRequestDto station) {
        return StationCreateResponseDto.toDtoEntity(stationRepository.save(station.toEntity()));
    }

    public StationListResponseDto list() {
        return StationListResponseDto.toDtoEntity(stationRepository.findAll());
    }

    public StationDetailResponseDto detail(long id) {
        Station station = stationRepository.findById(id).orElseThrow(IllegalAccessError::new);
        return StationDetailResponseDto.toDtoEntity(station);
    }

    public void delete(long id) {
        Optional<Station> station = stationRepository.findById(id);
        station.orElseThrow(IllegalAccessError::new).deleteStation();
    }
}
