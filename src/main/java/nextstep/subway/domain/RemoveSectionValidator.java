package nextstep.subway.domain;

import nextstep.subway.ui.BusinessException;

public class RemoveSectionValidator {

  public static void validate(final Sections sections, final Station station) {
    if (isNotContainsStation(sections, station)) {
      throw new BusinessException("노선에 속하지 않은 구간을 삭제할 수 없습니다.");
    }

    if (isOnlyRemainingStation(sections)) {
      throw new BusinessException("노선에 구간이 최소 하나 이상 존재해야 합니다.");
    }
  }

  // 노선에 속하지 않는 구간을 삭제할 수 없습니다.
  private static boolean isNotContainsStation(final Sections sections, final Station station) {
    return sections.getStations().stream()
        .noneMatch(it -> it.equals(station));
  }

  // 대상 구간이 노선의 유일한 구간인 경우 삭제할 수 없다.
  private static boolean isOnlyRemainingStation(final Sections sections) {
    return sections.size() == 1;
  }
}
