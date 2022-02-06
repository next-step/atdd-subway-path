package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {
  private final Sections sections = new Sections();
  private final Line line = new Line("2호선", "");
  private final Station 강남역 = new Station("강남역");
  private final Station 서초역 = new Station("서초역");



  @BeforeEach
  void setup(){
      Section section = new Section(line, 강남역, 서초역, 5);
      sections.addSection(section);
  }

  @Test
  @DisplayName("역 사이에 새로운 역을 등록할 수 있음")
  void addSectionSuccessfulTest(){
    // given
    final Station 교대역 = new Station("교대역");
    Section section = new Section(line, 강남역, 교대역, 2);

    // when
    sections.addSection(section);

    // then
    assertThat(sections.getSectionStations()).containsExactly(강남역, 교대역, 서초역);
  }

  @Test
  @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
  void addSectionWithIllegalDistanceTest(){
    // given
    final Station 교대역 = new Station("교대역");
    Section section = new Section(line, 강남역, 교대역, 5);

    // then
    assertThatThrownBy(() -> sections.addSection(section)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
  void addSectionWithDuplicateSectionSiteTest(){
    // given
    Section section = new Section(line, 강남역, 서초역, 5);

    // then
    assertThatThrownBy(() -> sections.addSection(section)).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
  void addSectionWithoutRegisteredStationsTest(){
    // given
    final Station 인덕원역 = new Station("인덕원역");
    final Station 정부과천청사역 = new Station("정부과천청사역");
    Section section = new Section(line, 인덕원역, 정부과천청사역, 2);

    // then
    assertThatThrownBy(() -> sections.addSection(section)).isInstanceOf(IllegalArgumentException.class);
  }
}