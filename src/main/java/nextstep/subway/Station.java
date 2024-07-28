package nextstep.subway;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Station {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 20, nullable = false)
  private String name;

  public Station() {
  }

  public Station(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    Station station = (Station)object;
    return Objects.equals(name, station.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
