package nextstep.subway.path.controller.dto;

import java.util.List;
import nextstep.subway.station.controller.dto.StationResponse;

public class PathResponse {
  private List<StationResponse> stations;
  private Long distance;

  public List<StationResponse> getStations() {
    return stations;
  }

  public Long getDistance() {
    return distance;
  }

  public PathResponse(List<StationResponse> stations, Long distance) {
    this.stations = stations;
    this.distance = distance;
  }
}
