package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import nextstep.subway.ui.BusinessException;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RemoveSectionValidatorTest {

  @DisplayName("구간 제거 검증 성공")
  @Test
  void validate() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 3);
    final var sections = new Sections();
    sections.addSection(new Section(null, stations.get(0), stations.get(1), 5));
    sections.addSection(new Section(null, stations.get(1), stations.get(2), 5));

    // when
    final var throwable = catchThrowable(() -> RemoveSectionValidator.validate(sections, stations.get(0).getId()));

    // then
    assertThat(throwable).isNull();
  }

  @DisplayName("노선에 속하지 않은 구간 제거 요청 시 에러 발생")
  @Test
  void validate_노선에_속하지_않은_구간_제거() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 3);
    final var sections = new Sections();
    sections.addSection(new Section(null, stations.get(0), stations.get(1), 5));

    // when
    final var throwable = catchThrowable(() -> RemoveSectionValidator.validate(sections, stations.get(2).getId()));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("노선에 속하지 않은 구간을 삭제할 수 없습니다.");
  }

  @DisplayName("노선에 유일하게 남은 구간 제거 요청 시 에러 발생")
  @Test
  void validate_유일하게_남은_구간_제거() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 2);
    final var sections = new Sections();
    sections.addSection(new Section(null, stations.get(0), stations.get(1), 5));

    // when
    final var throwable = catchThrowable(() -> RemoveSectionValidator.validate(sections, stations.get(0).getId()));

    // then
    assertThat(throwable).isInstanceOf(BusinessException.class)
        .hasMessageContaining("노선에 구간이 최소 하나 이상 존재해야 합니다.");
  }
}