package nextstep.subway.section;

public class AddFirstStrategy extends SectionAddStrategy{
  public AddFirstStrategy(Sections sections) {
    super(sections);
  }

  @Override
  public void add(final Section section) {
    final Section matchedSection = sections.findByUpSection(section.getDownStation()).orElseThrow(IllegalStateException::new);
    // 첫 구간이어야 한다.
    if (sections.hasNextSection(matchedSection)) {
      throw new IllegalArgumentException("첫 구간에만 추가할 수 있습니다");
    }

    sections.addOnlyList(section);
  }
}
