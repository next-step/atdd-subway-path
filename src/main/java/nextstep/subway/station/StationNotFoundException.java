package nextstep.subway.station;

public class StationNotFoundException extends RuntimeException{
  public StationNotFoundException(Long id) {
    super(String.format("id: %d Station is not existed", id));
  }
}
