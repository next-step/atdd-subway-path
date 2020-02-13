package atdd.station.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationList {
  private int stationCount;
  private List<Station> stations;
  public StationList(List<Station> stations) {
    this.stationCount = stations.size();
    this.stations = stations;
  }
}
