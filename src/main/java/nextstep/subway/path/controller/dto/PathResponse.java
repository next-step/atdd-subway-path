package nextstep.subway.path.controller.dto;

import java.util.List;
import nextstep.subway.station.controller.dto.StationResponse;

public class PathResponse {
  private List<StationResponse> stations;
  private Double distance;

  public List<StationResponse> getStations() {
    return stations;
  }

  public Double getDistance() {
    return distance;
  }

  public PathResponse(List<StationResponse> stations, Double distance) {
    this.stations = stations;
    this.distance = distance;
  }
}
