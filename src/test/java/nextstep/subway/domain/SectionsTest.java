package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

  @DisplayName("빈 노선에 구간을 추가한다.")
  @Test
  void addSection_빈노선에_구간_추가() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 2);
    final var sections = new Sections();

    // when
    sections.addSection(new Section(null, stations.get(0), stations.get(1), 5));

    // then
    assertThat(sections.size()).isEqualTo(1);
    assertThat(sections.getStations()).containsExactly(stations.get(0), stations.get(1));
    assertThat(sections.getStations()).hasSize(stations.size());
  }

  @DisplayName("노선의 맨 앞에 구간을 추가한다.")
  @Test
  void addSection_노선의_맨_앞에_구간_추가() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 4);
    final var sections = new Sections();
    for (int i = 2; i < stations.size(); i++) {
      sections.addSection(new Section(null, stations.get(i - 1), stations.get(i), 5));
    }

    // when
    sections.addSection(new Section(null, stations.get(0), stations.get(1), 5));

    // then
    assertThat(sections.size()).isEqualTo(stations.size() - 1);
    assertThat(sections.getStations()).containsExactly(stations.toArray(new Station[4]));
    assertThat(sections.getFirstStation().get()).isEqualTo(stations.get(0));
    assertThat(sections.getStations()).hasSize(stations.size());
  }

  @DisplayName("노선의 중간에 구간을 추가한다.")
  @Test
  void addSection_노선의_중간에_구간_추가() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 4);
    final var sections = new Sections();

    sections.addSection(new Section(null, stations.get(0), stations.get(2), 5));
    sections.addSection(new Section(null, stations.get(2), stations.get(3), 5));


    // when
    sections.addSection(new Section(null, stations.get(1), stations.get(2), 3));

    // then
    assertThat(sections.size()).isEqualTo(stations.size() - 1);
    assertThat(sections.getStations()).containsExactly(stations.toArray(new Station[4]));
    assertThat(sections.getLastStation().get()).isEqualTo(stations.get(stations.size() - 1));
    assertThat(sections.getStations()).hasSize(stations.size());
  }

  @DisplayName("노선의 맨 뒤에 구간을 추가한다.")
  @Test
  void addSection_노선의_맨_뒤에_구간_추가() {
    // given
    final var stations = FixtureUtil.getFixtures(Station.class, 4);
    final var sections = new Sections();
    for (int i = 1; i < stations.size() - 1; i++) {
      sections.addSection(new Section(null, stations.get(i - 1), stations.get(i), 5));
    }

    // when
    sections.addSection(new Section(null, stations.get(stations.size() - 2), stations.get(stations.size() - 1), 5));

    // then
    assertThat(sections.size()).isEqualTo(stations.size() - 1);
    assertThat(sections.getStations()).containsExactly(stations.toArray(new Station[4]));
    assertThat(sections.getLastStation().get()).isEqualTo(stations.get(stations.size() - 1));
    assertThat(sections.getStations()).hasSize(stations.size());
  }
}