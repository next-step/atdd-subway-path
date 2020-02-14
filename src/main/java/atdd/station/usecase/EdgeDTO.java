package atdd.station.usecase;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EdgeDTO implements Serializable {
  private Long id;
  private Long lineID;
  private int elapsedTime;
  private Float distance;
  private Long sourceStationID;
  private Long targetStationID;
}
