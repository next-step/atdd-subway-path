package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import nextstep.subway.ui.BusinessException;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddSectionValidatorTest {

  @DisplayName("구간 추가 검증 성공")
  @Test
  void validate() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 3);
    final var sections = new Sections();
    sections.addSection(new Section(null, stations.get(0), stations.get(1), 5));

    // when
    final var throwable = catchThrowable(() ->
        AddSectionValidator.validate(
            sections,
            new Section(null, stations.get(1), stations.get(2), 5))
    );

    // then
    assertThat(throwable).isNull();
  }

  @DisplayName("노선에 존재하는 구간과 중복된 구간을 추가 할 수 없다.")
  @Test
  void validate_같은_구간_중복_추가() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 2);
    final var sections = new Sections();
    sections.addSection(new Section(null, stations.get(0), stations.get(1), 5));

    // when
    final var throwable = catchThrowable(() ->
        AddSectionValidator.validate(
            sections,
            new Section(null, stations.get(0), stations.get(1), 3))
    );

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("이미 노선에 속한 구간을 추가할 수 없습니다.");
  }

  @DisplayName("노선에 존재하는 구간과 연결할 수 없는 구간을 추가 할 수 없다.")
  @Test
  void validate_기존_구간과_연결_불가() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 4);
    final var sections = new Sections();
    sections.addSection(new Section(null, stations.get(0), stations.get(1), 5));

    // when
    final var throwable = catchThrowable(() ->
        AddSectionValidator.validate(
            sections,
            new Section(null, stations.get(2), stations.get(3), 5)
        )
    );

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("노선에 새로운 구간과 이어지는 역이 없습니다.");
  }

  @DisplayName("새 구간의 거리가 분할 될 구간보다 길 수 없다.")
  @Test
  void validate_구간_길이_검증_실패() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 4);
    final var sections = new Sections();
    sections.addSection(new Section(null, stations.get(0), stations.get(1), 5));
    sections.addSection(new Section(null, stations.get(1), stations.get(3), 5));

    // when
    final var throwable = catchThrowable(() ->
        AddSectionValidator.validate(
            sections,
            new Section(null, stations.get(1), stations.get(2), 6)
        )
    );

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("새 구간이 추가 될 구간의 길이가 새 구간보다 짧습니다.");
  }
}