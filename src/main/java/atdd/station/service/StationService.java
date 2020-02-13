package atdd.station.service;

import atdd.station.entity.Station;
import atdd.station.entity.StationList;
import atdd.station.repository.StationRepository;
import atdd.station.usecase.StationDTO;
import atdd.station.usecase.StationListDTO;
import atdd.station.usecase.StationUseCase;
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
    ).get();
    return stationModelMapper.EntityToDTO(station);
  }

  @Override
  public StationListDTO getAllStation() {
    return stationModelMapper.ListEntityToDTO(
        new StationList(stationRepository.findAll())
    );
  }

  @Override
  public StationDTO getStation(StationDTO stationDTO) {
    return stationModelMapper.EntityToDTO(
        stationRepository.getByname(
            stationDTO.getName()
        ).get()
    );
  }

  @Override
  public void removeStation(StationDTO stationDTO) {
    Station target = stationRepository.getByname(stationDTO.getName()).get();
    stationRepository.removeByid(
        target.getId()
    );
  }
}
