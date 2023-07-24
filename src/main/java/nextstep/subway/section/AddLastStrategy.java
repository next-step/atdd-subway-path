package nextstep.subway.section;

public class AddLastStrategy extends SectionAddStrategy{
  public AddLastStrategy(Sections sections) {
    super(sections);
  }

  @Override
  public void add(final Section section) {
    final Section matchedSection = sections.findByDownSection(section.getUpStation()).orElseThrow(IllegalArgumentException::new);

    if (sections.findByUpSection(matchedSection.getDownStation()).isPresent()) {
      throw new IllegalArgumentException("마지막 구간에만 추가할 수 있습니다");
    }

    this.sections.addOnlyList(section);
  }
}
