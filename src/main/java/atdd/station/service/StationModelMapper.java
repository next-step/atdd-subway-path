package atdd.station.service;

import atdd.station.entity.StationEntity;
import atdd.station.usecase.StationDTO;

public class StationModelMapper {
  public StationEntity DTOToEntity(StationDTO stationDTO) {
    return new StationEntity(
        stationDTO.getName()
    );
  }

  public StationDTO EntityToDTO(StationEntity stationEntity){
    return new StationDTO(
        stationEntity.getId(),
        stationEntity.getName()
    );
  }
}
