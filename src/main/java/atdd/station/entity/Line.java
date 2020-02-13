package atdd.station.entity;

import java.sql.Time;
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
@Table(name="LINE")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Line {
  @Id
  @Column(name="ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="name")
  private String name;

  @Column(name="start_time")
  private LocalTime startLocalTime;

  @Column(name="last_time")
  private LocalTime lastLocalTime;

  @Column(name="time_interval")
  private int timeInterval;

  @Column(name="extra_fare")
  private int extra_fare;
}
