package nextstep.subway.domain;

import lombok.Getter;
import org.jgrapht.graph.DefaultWeightedEdge;


public class SectionEdge extends DefaultWeightedEdge {

  @Getter
  private final Section section;

  public SectionEdge(Section section) {
    this.section = section;
  }

  public static SectionEdge of(Section section) {
    return new SectionEdge(section);
  }
}
