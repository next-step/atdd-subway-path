package atdd.station.usecase;

public interface StationUseCase {
  StationDTO addStation(StationDTO stationDTO);
  StationListDTO getAllStation();
  StationDTO getStation(StationDTO stationDTO);
  void removeStation(StationDTO stationDTO);
}
