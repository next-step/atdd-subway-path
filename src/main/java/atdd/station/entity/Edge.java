package atdd.station.entity;

import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="EDGE")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Edge {
  @Id
  @Column(name="ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="line_id")
  private Long lineID;

  @Column(name="elapsed_time")
  private int elapsedTime;

  @Column(name="distance")
  private Float distance;

  @Column(name="source_station_id")
  private Long sourceStationID;

  @Column(name="target_station_id")
  private Long targetStationID;
}
