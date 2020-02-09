package atdd.station.service;

import atdd.station.entity.StationEntity;
import atdd.station.entity.StationListEntity;
import atdd.station.usecase.StationDTO;
import atdd.station.usecase.StationListDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    List<StationEntity> entities = stationListDTO.getStations()
        .stream()
        .map(
            stationDTO -> this.DTOToEntity(stationDTO)
        ).collect(
            Collectors.toList()
        );
    return new StationListEntity(entities.size(), entities);
  }

  public StationListDTO ListEntityToDTO(StationListEntity stationListEntity) {
    List<StationDTO> stationDTOS = stationListEntity.getStations()
        .stream()
        .map(
            stationEntity -> this.EntityToDTO(stationEntity)
        ).collect(
            Collectors.toList()
        );
    return new StationListDTO(stationDTOS.size(), stationDTOS);
  }
}
