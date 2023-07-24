package nextstep.subway.section;

public class AddLastStrategy extends SectionAddStrategy{
  public AddLastStrategy(Sections sections) {
    super(sections);
  }

  @Override
  public void add(final Section section) {
    final Section matchedSection = sections.findByDownSection(section.getUpStation()).orElseThrow(IllegalArgumentException::new);

    if (sections.findByUpSection(matchedSection.getDownStation()).isPresent()) {
      throw new IllegalStateException();
    }

    this.sections.addOnlyList(section);
  }
}
