package nextstep.subway.path.domain;
import java.util.List;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

public class Path {

  private int distance;
  private List<Station> stations;

  public Path(int distance, List<Station> stations) {
    this.distance = distance;
    this.stations = stations;
  }


  public int getDistance() {
    return distance;
  }

  public List<Station> getStations() {
    return stations;
  }



}
