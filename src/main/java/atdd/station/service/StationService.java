package atdd.station.service;

import atdd.station.entity.StationEntity;
import atdd.station.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationService implements IStationService {
  @Autowired
  StationRepository stationRepository;

  @Override
  public StationDTO addStation(StationDTO stationDTO) {
    StationEntity station = stationRepository.save(new StationEntity(stationDTO.name));
    return new StationDTO(station.Name);
  }
}
