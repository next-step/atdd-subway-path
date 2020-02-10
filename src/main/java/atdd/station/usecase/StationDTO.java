package atdd.station.usecase;

import java.io.Serializable;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationDTO implements Serializable {
  private Long id;
  private String name;

  public StationDTO(String Name) {
    this.name = Name;
  }
}
