package nextstep.subway.applicaion.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PathResponse {

  private int distance = Integer.MAX_VALUE;

  private List<StationResponse> stations = new ArrayList<>();

  public void changeStations(List<StationResponse> stations) {
    this.stations = stations;
  }

  public void changeDistance(int distance) {
    this.distance= distance;
  }
}
