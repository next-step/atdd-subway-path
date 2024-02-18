package nextstep.subway.domain;

import nextstep.subway.ui.BusinessException;

public class RemoveSectionValidator {

  public static void validate(final Sections sections) {
    // 대상 구간이 노선의 유일한 구간인 경우 삭제할 수 없다.
    if (sections.size() == 1) {
      throw new BusinessException("노선에 구간이 최소 하나 이상 존재해야 합니다.");
    }
  }
}
