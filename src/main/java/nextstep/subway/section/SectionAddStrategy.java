package nextstep.subway.section;

import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;

import java.util.List;
import java.util.Optional;

public abstract class SectionAddStrategy {
  protected final Sections sections;

  public SectionAddStrategy(Sections sections) {
    this.sections = sections;
  }


  abstract public void add(Section section);
}
