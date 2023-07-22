package nextstep.subway.section;

import nextstep.subway.line.Line;
import nextstep.subway.station.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionUnitTest {

  @Test
  @DisplayName("상행역을 변경하면 새로운 거리와 상행역이 적용된다.")
  void changeUpStation() {
    final Section section = new Section(new Line("분당선", "red"),
        new Station("야탑역"),
        new Station("이매역"),
        3L
    );

    section.changeUpStation(new Station("서현역"), 6L);

    assertThat(section.getUpStation())
        .extracting(Station::getName)
        .isEqualTo("서현역");

    assertThat(section.getDistance())
        .isEqualTo(6L);
  }
}