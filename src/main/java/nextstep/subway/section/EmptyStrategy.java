package nextstep.subway.section;

public class EmptyStrategy extends SectionAddStrategy{
  public EmptyStrategy(Sections sections) {
    super(sections);
  }

  @Override
  public void add(final Section section) {
    this.sections.addOnlyList(section);
  }
}
