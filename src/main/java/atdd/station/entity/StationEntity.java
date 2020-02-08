package atdd.station.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "STATION")
@NoArgsConstructor
@AllArgsConstructor
public class StationEntity implements Serializable {
  @Id
  @Getter
  @Column(name="ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "NAME")
  public String Name;

  public StationEntity(String name) {
    this.Name = name;
  }
}
