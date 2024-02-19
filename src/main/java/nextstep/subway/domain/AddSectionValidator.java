package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.ui.BusinessException;

public class AddSectionValidator {

  public static void validate(final Sections sections, final Section newSection) {
    if (isDuplicated(sections, newSection)) {
      throw new BusinessException("이미 노선에 속한 구간을 추가할 수 없습니다.");
    }

    if (isNotConnectable(sections, newSection)) {
      throw new BusinessException("노선에 새로운 구간과 이어지는 역이 없습니다.");
    }

    if (isInvalidDistance(sections, newSection)) {
      throw new BusinessException("새 구간이 추가 될 구간의 길이가 새 구간보다 짧습니다.");
    }
  }

  private static boolean isInvalidDistance(final Sections sections, final Section newSection) {
    final var beforeSection = sections.getBeforeSection(newSection);
    if (beforeSection.isPresent()) {
      return beforeSection.get().getDistance() <= newSection.getDistance();
    }

    final var afterSection = sections.getAfterSection(newSection);
    if (afterSection.isPresent()) {
      return afterSection.get().getDistance() <= newSection.getDistance();
    }

    return false;
  }

  // 노선에 속한 역에 새로운 구간의 상행역과 하행역이 모두 포함되는 중복 구간을 추가할 수 없다.
  private static boolean isDuplicated(
      final Sections sections,
      final Section newSection
  ) {
    final var stationIdsOfLine = sections.getStations().stream()
        .map(Station::getId)
        .collect(Collectors.toSet());

    final var upStationIdOfNewSection = newSection.getUpStation().getId();
    final var downStationIdOfNewSection = newSection.getDownStation().getId();

    return stationIdsOfLine.containsAll(List.of(upStationIdOfNewSection, downStationIdOfNewSection));
  }

  // 노선에 속한 역에 새로운 구간의 상행역 혹은 하행역 중 하나는 포함되어야 한다.
  private static boolean isNotConnectable(
      final Sections sections,
      final Section newSection
  ) {
    // 노선에 구간이 없으면 연결여부 고려하지 않음
    if (sections.isEmpty()) {
      return false;
    }

    final var stationIdsOfLine = sections.getStations().stream()
        .map(Station::getId)
        .collect(Collectors.toSet());

    final var upStationIdOfNewSection = newSection.getUpStation().getId();
    final var downStationIdOfNewSection = newSection.getDownStation().getId();

    return !stationIdsOfLine.contains(upStationIdOfNewSection) && !stationIdsOfLine.contains(downStationIdOfNewSection);
  }
}
