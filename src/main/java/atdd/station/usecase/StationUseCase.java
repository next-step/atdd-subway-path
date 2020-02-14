package atdd.station.usecase;

public interface StationUseCase {
  StationDTO addStation(StationDTO stationDTO);
  StationListDTO getAllStation();
  StationDTO getStation(Long stationID);
  void removeStation(Long StationID);
}
