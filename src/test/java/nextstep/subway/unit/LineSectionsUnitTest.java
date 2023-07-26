package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간 단위테스트")
class LineSectionsUnitTest {

  @Test
  @DisplayName("아무것도 생성하지 않으면 빈 리스트를 리턴한다")
  void sectionsEmptyTest() {
    final Sections sections = new Sections();

    assertThat(sections.getSections())
        .isEmpty();
  }

  @Test
  @DisplayName("하나의 구간을 추가하면 정상적으로 추가된다")
  void addSectionTest() {
    final Sections sections = new Sections();
    final Section section = new Section(
        new Line("분당선", "RED"),
        new Station("야탑역"),
        new Station("이매역"),
        3L
    );
    sections.addSection(section);

    assertThat(sections.getSections())
        .hasSize(1)
        .containsOnly(section);
  }

  @Test
  @DisplayName("두개의 구간을 이어지도록 추가하면 정상적으로 추가된다")
  void addTwoSectionTest() {
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    line.addSection(new Station(1L, "야탑역"),
        new Station(2L, "이매역"),
        3L);

    line.addSection(new Station(2L, "이매역"),
        new Station(3L, "서현역"),
        4L);

    assertThat(line.getStations())
        .extracting(Station::getName)
        .containsExactly("야탑역", "이매역", "서현역");
  }

  @Test
  @DisplayName("두개의 구간을 이어지지 않도록 추가하면 에러가 발생한다")
  void addTwoSectionNotRelationTest() {
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    line.addSection(new Station(1L, "야탑역"),
        new Station(2L, "이매역"),
        3L);

    Assertions.assertThatThrownBy(() -> line.addSection(new Station(5L, "서현역"),
        new Station(6L, "판교역"),
        4L)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("두개의 구간을 동일한 상행역으로 추가할 때, 거리를 초과하게 추가하면 에러가 발생한다")
  void addTwoSectionOverDistanceTest() {
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    line.addSection(new Station(1L, "야탑역"),
        new Station(2L, "이매역"),
        3L);

    Assertions.assertThatThrownBy(() -> line.addSection(new Station(1L, "야탑역"),
        new Station(6L, "판교역"),
        4L)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("추가구간의 상행과 기존 상행이 일치시 상행역이 추가된다.")
  void addTwoSectionBeforeTest() {
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    line.addSection(new Station(1L, "야탑역"),
        new Station(2L, "이매역"),
        3L);

    line.addSection(new Station(5L, "모란역"),
        new Station(1L, "야탑역"),
        4L);

    assertThat(line.getStations())
        .extracting(Station::getName)
        .containsExactly("모란역", "야탑역", "이매역");
  }

  @Test
  @DisplayName("상행, 하행이 모두 일치하는 구간을 추가하면 에러가 발생한다")
  void addEqualsTwoSection() {
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    line.addSection(new Station(1L, "야탑역"),
        new Station(2L, "이매역"),
        3L);

    Assertions.assertThatThrownBy(() -> line.addSection(new Station(1L, "야탑역"),
        new Station(2L, "이매역"),
        3L)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("하행이 동일한 역으로 구간을 추가하면 성공한다")
  void addEqualsDownStation() {
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    line.addSection(new Station(1L, "야탑역"),
        new Station(2L, "이매역"),
        3L);

    line.addSection(new Station(5L, "서현역"),
        new Station(2L, "이매역"),
        1L);

    assertThat(line.getStations())
        .extracting(Station::getName)
        .containsExactly("야탑역", "서현역", "이매역");
  }

  @Test
  @DisplayName("하행이 동일한 역이지만, 거리를 기존보다 크게 설정하면 구간 추가시 에러가 발생한다")
  void addEqualsDownStationOverDistance() {
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    line.addSection(new Station(1L, "야탑역"),
        new Station(2L, "이매역"),
        3L);

    assertThatThrownBy(() -> line.addSection(new Station(5L, "서현역"),
        new Station(2L, "이매역"),
        4L)).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  @DisplayName("2개의 구간이 있을 때, 첫 구간의 상행역을 삭제할 수 있다")
  void deleteFirstStation() {
    // given
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    final Station 야탑역 = new Station(1L, "야탑역");
    final Station 이매역 = new Station(2L, "이매역");
    final Station 서현역 = new Station(3L, "서현역");

    line.addSection(야탑역,
        이매역,
        3L);
    line.addSection(이매역,
        서현역,
        3L);

    // when
    line.deleteSection(야탑역);

    // then
    assertThat(line.getStations())
        .extracting(Station::getName)
        .containsExactly("이매역", "서현역");
  }

  @Test
  @DisplayName("2개의 구간이 있을 때, 중간역을 삭제할 수 있다")
  void deleteBetweenStation() {
    // given
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    final Station 야탑역 = new Station(1L, "야탑역");
    final Station 이매역 = new Station(2L, "이매역");
    final Station 서현역 = new Station(3L, "서현역");

    line.addSection(야탑역,
        이매역,
        3L);
    line.addSection(이매역,
        서현역,
        3L);

    // when
    line.deleteSection(이매역);

    // then
    assertThat(line.getStations())
        .extracting(Station::getName)
        .containsExactly("야탑역", "서현역");
  }

  @Test
  @DisplayName("2개의 구간이 있을 때, 마지막 역을 삭제할 수 있다")
  void deleteLastStation() {
    // given
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    final Station 야탑역 = new Station(1L, "야탑역");
    final Station 이매역 = new Station(2L, "이매역");
    final Station 서현역 = new Station(3L, "서현역");

    line.addSection(야탑역,
        이매역,
        3L);
    line.addSection(이매역,
        서현역,
        3L);

    // when
    line.deleteSection(서현역);

    // then
    assertThat(line.getStations())
        .extracting(Station::getName)
        .containsExactly("야탑역", "이매역");
  }

  @Test
  @DisplayName("1개의 구간이 있을 때, 역을 삭제하면 에러가 발생한다")
  void deleteStationFail() {
    // given
    final Line line = new Line(0L, "분당선", "RED", new ArrayList<>());

    final Station 야탑역 = new Station(1L, "야탑역");
    final Station 이매역 = new Station(2L, "이매역");

    line.addSection(야탑역,
        이매역,
        3L);
    // when
    assertThatThrownBy(() -> line.deleteSection(야탑역))
        .isInstanceOf(IllegalStateException.class);
  }

}