package nextstep.subway.domain;

import java.util.List;
import lombok.Getter;

public class Path {

  @Getter
  private Sections sections;
  @Getter
  private Long distance;

  public Path(Sections sections, Long distance) {
    this.sections = sections;
    this.distance = distance;
  }

  public static Path of(Sections sections, Long distance) {
    return new Path(sections, distance);
  }

  public List<Station> getStations() {
    return sections.getStations();
  }
}
