package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.station.domain.Station;

@Entity
public class Section implements Comparable<Section>{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "line_id")
  private Line line;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "up_station_id")
  private Station upStation;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "down_station_id")
  private Station downStation;

  private int distance;

  public Section() {
  }

  public Section(Line line, Station upStation, Station downStation, int distance) {
    this.line = line;
    this.upStation = upStation;
    this.downStation = downStation;
    this.distance = distance;
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

  public Station getDownStation() {
    return downStation;
  }

  public int getDistance() {
    return distance;
  }

  @Override
  public int compareTo(Section section) {
    if(getDownStation().equals(section.getUpStation())) return -1;
    if(getUpStation().equals(section.getDownStation())) return 1;
    return 0;
  }
}