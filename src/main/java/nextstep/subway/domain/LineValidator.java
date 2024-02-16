package nextstep.subway.domain;

import java.util.Objects;
import nextstep.subway.ui.BusinessException;

public class LineValidator {

  public static void checkSectionForAddition(final Line line, final Section section) {
    // 새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다.
    final var lastStationIdOfLine = line.getSections().get(line.getSections().size() - 1).getDownStation().getId();
    if (!Objects.equals(lastStationIdOfLine, section.getUpStation().getId())) {
      throw new BusinessException("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다.");
    }

    // 이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.
    line.getSections().stream()
        .filter(it ->
            it.getUpStation().getId().equals(section.getDownStation().getId())
                || it.getUpStation().getId().equals(section.getUpStation().getId())
        )
        .findAny()
        .ifPresent(station -> {
          throw new BusinessException("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다.");
        });
  }

  public static void checkSectionForRemove(final Line line, final Section section) {
    // 지하철 노선에 등록된 역(하행 종점역)만 제거할 수 있다. 즉, 마지막 구간만 제거할 수 있다.
    final var lastStationIdOfLine = line.getSections().get(line.getSections().size() - 1).getDownStation().getId();
    if (!Objects.equals(lastStationIdOfLine, section.getDownStation().getId())) {
      throw new BusinessException("노선의 마지막 구간만 제거할 수 있습니다.");
    }

    // 이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.
    if (line.getSections().size() == 1) {
      throw new BusinessException("노선에 구간이 최소 하나 이상 존재해야 합니다.");
    }
  }
}
