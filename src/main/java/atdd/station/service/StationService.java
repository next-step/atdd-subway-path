package atdd.station.service;

import atdd.station.entity.Station;
import atdd.station.entity.StationList;
import atdd.station.repository.StationRepository;
import atdd.station.usecase.StationDTO;
import atdd.station.usecase.StationListDTO;
import atdd.station.usecase.StationUseCase;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StationService implements StationUseCase {

  @Autowired
  StationRepository stationRepository;

  private final StationModelMapper stationModelMapper = new StationModelMapper();

  @Override
  public StationDTO addStation(StationDTO stationDTO) {
    Station station = stationRepository.save(
        stationModelMapper.DTOToEntity(stationDTO)
    );
    return stationModelMapper.EntityToDTO(station);
  }

  @Override
  public StationListDTO getAllStation() {
    Iterable<Station> stations = stationRepository.findAll();
    List<StationDTO> stationDTOS = StreamSupport
        .stream(stations.spliterator(), false)
        .map(
            station -> stationModelMapper.EntityToDTO(station)
        ).collect(Collectors.toList());
    return new StationListDTO(stationDTOS.size(), stationDTOS);
  }

  @Override
  public StationDTO getStation(Long stationID) {
    return stationModelMapper.EntityToDTO(
        stationRepository.findById(stationID).get()
    );
  }

  @Override
  public void removeStation(Long stationID) {
    stationRepository.deleteById(stationID);
  }
}
