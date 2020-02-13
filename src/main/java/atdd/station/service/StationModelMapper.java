package atdd.station.service;

import atdd.station.entity.Station;
import atdd.station.entity.StationList;
import atdd.station.usecase.StationDTO;
import atdd.station.usecase.StationListDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StationModelMapper {

  public Station DTOToEntity(StationDTO stationDTO) {
    return new Station(
        stationDTO.getName()
    );
  }

  public StationDTO EntityToDTO(Station station) {
    return new StationDTO(
        station.getId(),
        station.getName()
    );
  }

  public StationList ListDTOToEntity(StationListDTO stationListDTO) {
    List<Station> entities = stationListDTO.getStations()
        .stream()
        .map(
            stationDTO -> this.DTOToEntity(stationDTO)
        ).collect(
            Collectors.toList()
        );
    return new StationList(entities.size(), entities);
  }

  public StationListDTO ListEntityToDTO(StationList stationList) {
    List<StationDTO> stationDTOS = stationList.getStations()
        .stream()
        .map(
            stationEntity -> this.EntityToDTO(stationEntity)
        ).collect(
            Collectors.toList()
        );
    return new StationListDTO(stationDTOS.size(), stationDTOS);
  }
}
