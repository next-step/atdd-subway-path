package atdd.station.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationListEntity {
  private int stationCount;
  private List<StationEntity> stations;
}
