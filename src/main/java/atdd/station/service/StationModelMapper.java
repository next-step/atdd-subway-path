package atdd.station.service;

import atdd.station.entity.StationEntity;
import atdd.station.entity.StationListEntity;
import atdd.station.usecase.StationDTO;
import atdd.station.usecase.StationListDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StationModelMapper {

  public StationEntity DTOToEntity(StationDTO stationDTO) {
    return new StationEntity(
        stationDTO.getName()
    );
  }

  public StationDTO EntityToDTO(StationEntity stationEntity) {
    return new StationDTO(
        stationEntity.getId(),
        stationEntity.getName()
    );
  }

  public StationListEntity ListDTOToEntity(StationListDTO stationListDTO) {
    List<StationEntity> entities = new ArrayList<>();
    for(StationDTO stationDTO : stationListDTO.getStations()) {
      entities.add(
          this.DTOToEntity(stationDTO)
      );
    }
    return new StationListEntity(entities.size(), entities);
  }

  public StationListDTO ListEntityToDTO(StationListEntity stationListEntity) {
    List<StationDTO> stationDTOS = new ArrayList<>();
    for(StationEntity stationEntity : stationListEntity.getStations()) {
      stationDTOS.add(
          this.EntityToDTO(stationEntity)
      );
    }
    return new StationListDTO(stationDTOS.size(), stationDTOS);
  }
}
