package nextstep.subway.section;

import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.*;

@Entity
public class Section {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "line_id")
  private Line line;

  @ManyToOne
  @JoinColumn(name = "up_station_id")
  private Station upStation;

  @ManyToOne
  @JoinColumn(name = "down_station_id")
  private Station downStation;

  @Column(nullable = false)
  private Long distance;


  public Station getDownStation() {
    return downStation;
  }


  public Long getId() {
    return id;
  }

  public Line getLine() {
    return line;
  }

  public Station getUpStation() {
    return upStation;
  }

  public Long getDistance() {
    return distance;
  }

  protected Section() {
  }

  public Section(Line line, Station upStation, Station downStation, long distance) {
    this.line = line;
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = distance;
  }

  public void changeUpStation(Station upStation, long newDistance) {
    this.upStation = upStation;
    this.distance = newDistance;
  }

  public void changeDownStation(Station downStation, long newDistance) {
    this.downStation = downStation;
    this.distance = newDistance;
  }

  public boolean isUpStation(Station station) {
    return this.upStation.equals(station);
  }

  public boolean isDownStation(Station station) {
    return this.downStation.equals(station);
  }

  public boolean isDividableDistance(long distance) {
    return this.distance > distance;
  }

  @Override
  public String toString() {
    return "Section{" +
        "id=" + id +
        ", line=" + line.getId() +
        ", upStation=" + upStation +
        ", downStation=" + downStation +
        ", distance=" + distance +
        '}';
  }
}
