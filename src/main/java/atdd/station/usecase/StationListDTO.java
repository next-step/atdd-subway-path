package atdd.station.usecase;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationListDTO implements Serializable {
  int totalStationCount;
  List<StationDTO> stations;
}
