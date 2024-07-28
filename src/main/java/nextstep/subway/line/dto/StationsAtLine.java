package nextstep.subway.line.dto;

import nextstep.subway.Station;

public class StationsAtLine {
  private Station upStation;

  private Station downStation;

  public StationsAtLine() {
  }

  public StationsAtLine(Station upStation, Station downStation) {
    this.upStation = upStation;
    this.downStation = downStation;
  }

  public Station getUpStation() {
    return upStation;
  }

  public Station getDownStation() {
    return downStation;
  }
}
