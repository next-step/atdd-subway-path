package nextstep.subway.line;

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
  private Section downStation;

  @Column(nullable = false)
  private Long distance;

  public void setDownStation(Section nextLineStation) {
    this.downStation = nextLineStation;
  }

  public Section getDownStation() {
    return downStation;
  }

  public boolean isLastStation() {
    return downStation == null;
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

  protected Section() {
  }

  public Section(Line line, Station upStation, long distance) {
    this.line = line;
    this.upStation = upStation;
    this.distance = distance;
  }

  public void removeNextLineStation() {
    this.downStation = null;
  }
}
