package atdd.station.service;

import atdd.station.entity.StationEntity;
import atdd.station.repository.StationRepository;
import atdd.station.usecase.StationDTO;
import atdd.station.usecase.StationUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationService implements StationUseCase {
  @Autowired
  StationRepository stationRepository;

  @Override
  public StationDTO addStation(StationDTO stationDTO) {
    StationModelMapper modelMapper = new StationModelMapper();
    StationEntity stationEntity = stationRepository.save(
        modelMapper.DTOToEntity(stationDTO)
    );
    return modelMapper.EntityToDTO(stationEntity);
  }
}
