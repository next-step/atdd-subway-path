package nextstep.subway.line;

import nextstep.subway.Station;

import javax.persistence.*;

@Entity
public class LineStation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "line_id")
  private Line line;

  @ManyToOne
  @JoinColumn(name = "station_id")
  private Station station;

  @ManyToOne
  @JoinColumn(name = "next_line_station_id")
  private LineStation nextLineStation;

  @Column(nullable = false)
  private Long distance = 0L;

  public void setNextLineStation(LineStation nextLineStation) {
    this.nextLineStation = nextLineStation;
  }

  public LineStation getNextLineStation() {
    return nextLineStation;
  }

  public boolean isLastStation() {
    return nextLineStation == null;
  }

  public Long getId() {
    return id;
  }

  public Line getLine() {
    return line;
  }

  public Station getStation() {
    return station;
  }

  protected LineStation() {
  }

  public LineStation(Line line, Station station, long distance) {
    this.line = line;
    this.station = station;
    this.distance = distance;
  }

  public void removeNextLineStation() {
    this.nextLineStation = null;
  }
}
