package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SectionsTest {

  private Line line;
  private Station 강남역;
  private Station 역삼역;
  private Station 선릉역;

  @BeforeEach
  void 기본_값_준비() {
    line =  new Line();
    강남역 = new Station("강남역");
    역삼역 = new Station("역삼역");
    선릉역 = new Station("선릉역");
  }

  @Test
  void 구간_추가() {
    Sections sections = new Sections();
    Section section = 신규_구간_생성(강남역, 역삼역, 10);

    sections.addSection(line, section);

    assertThat(sections.getSections()).contains(section);
  }

  @Test
  void 구간_중간에_새로운_구간_추가() {
    Sections sections = new Sections();
    Section section1 = 신규_구간_생성(강남역, 선릉역, 20);
    Section section2 = 신규_구간_생성(역삼역, 선릉역, 10);
    sections.addSection(line, section1);
    sections.addSection(line, section2);

    List<Station> stations = sections.getStations();

    assertThat(stations.stream().map(Station::getName).collect(Collectors.toList()))
        .containsExactly(강남역.getName(), 역삼역.getName(), 선릉역.getName());
  }

  @Test
  void 구간_내_역_조회() {
    Sections sections = new Sections();
    Section section1 = 신규_구간_생성(강남역, 역삼역, 10);
    Section section2 = 신규_구간_생성(역삼역, 선릉역, 10);
    sections.addSection(line, section1);
    sections.addSection(line, section2);

    List<Station> stations = sections.getStations();

    assertThat(stations.stream().map(Station::getName).collect(Collectors.toList()))
        .containsExactly(강남역.getName(), 역삼역.getName(), 선릉역.getName());
  }

  private Section 신규_구간_생성(Station upStation, Station downStation, Integer distance) {
    return new Section(line, upStation, downStation,distance);
  }
}
