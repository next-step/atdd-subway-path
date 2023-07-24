package nextstep.subway.section;

public abstract class SectionAddStrategy {
  protected final Sections sections;

  public SectionAddStrategy(Sections sections) {
    this.sections = sections;
  }


  abstract public void add(Section section);
}
