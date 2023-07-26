package nextstep.subway.section;

public class DivideEqUpStationStrategy extends SectionAddStrategy{
  public DivideEqUpStationStrategy(Sections sections) {
    super(sections);
  }

  @Override
  public void add(final Section section) {
    final Section matchedSection = sections.findByUpStation(section.getUpStation())
        .orElseThrow(IllegalAccessError::new);

    // 하행역이 같을 수 없다.
    if (matchedSection.isDownStation(section.getDownStation())) {
      throw new IllegalArgumentException("상행역, 하행역이 동시에 일치하는 구간을 추가할 수 없습니다.");
    }

    // 거리가 기존 구간보다 클 수 없다
    if (!matchedSection.isDividableDistance(section.getDistance())) {
      throw new IllegalArgumentException("기존 구간보다 큰 거리로 나눌 수 없습니다.");
    }

    matchedSection.changeUpStation(section.getDownStation(), matchedSection.getDistance() - section.getDistance());
    this.sections.addOnlyList(section);
  }
}
