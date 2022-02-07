package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.exception.DuplicateCreationException;
import nextstep.subway.exception.IllegalAddSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.SetupList.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
  private final Sections sections = new Sections();

  @BeforeEach
  void setup() {
    Section section = new Section(이호선, 강남역, 서초역, 5);
    sections.addSection(section);
  }

  @Test
  @DisplayName("역 사이에 새로운 역을 등록할 수 있음")
  void addUpSectionTest() {
    // given
    Section section = new Section(이호선, 역삼역, 강남역, 2);

    // when
    sections.addSection(section);

    // then
    assertThat(sections.getSectionStations()).containsExactly(역삼역, 강남역, 서초역);
  }

  @Test
  @DisplayName("역 사이에 새로운 역을 등록할 수 있음")
  void addSectionSuccessfulTest() {
    // given
    Section section = new Section(이호선, 강남역, 교대역, 2);

    // when
    sections.addSection(section);

    // then
    assertThat(sections.getSectionStations()).containsExactly(강남역, 교대역, 서초역);
  }

  @Test
  @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
  void addSectionWithIllegalDistanceTest() {
    // given
    Section section = new Section(이호선, 강남역, 교대역, 5);

    // then
    assertThatThrownBy(() -> sections.addSection(section)).isInstanceOf(IllegalAddSectionException.class);
  }

  @Test
  @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
  void addSectionWithDuplicateSectionSiteTest() {
    // given
    Section section = new Section(이호선, 강남역, 서초역, 5);

    // then
    assertThatThrownBy(() -> sections.addSection(section)).isInstanceOf(DuplicateCreationException.class);
  }

  @Test
  @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
  void addSectionWithoutRegisteredStationsTest() {
    // given
    Section section = new Section(이호선, 인덕원역, 정부과천청사역, 2);

    // then
    assertThatThrownBy(() -> sections.addSection(section)).isInstanceOf(IllegalAddSectionException.class);
  }
}