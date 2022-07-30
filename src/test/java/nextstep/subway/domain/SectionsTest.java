package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SectionsTest {

  private static final String DONONGSTATIONNAME = "도농역";
  private static final String GOORISTATIONNAME = "구리역";
  private static final String DUCKSOSTATIONNAME = "덕소역";

  private static final String FIRSTLINENAME = "1호선";

  private static final String BLUE = "blue";

  private Station donongStation;
  private Station gooriStation;
  private Station ducksoStation;

  private Line line;

  private Section firstSection;
  private Section secondSection;

  private Sections sections;

  @BeforeEach
  void setup() {
    donongStation = new Station(DONONGSTATIONNAME);
    gooriStation = new Station(GOORISTATIONNAME);
    ducksoStation = new Station(DUCKSOSTATIONNAME);

    line = new Line(FIRSTLINENAME, BLUE);

    firstSection = new Section(line, donongStation, gooriStation, 10);
    secondSection = new Section(line, donongStation, ducksoStation, 5);

    sections = new Sections();

    sections.addSection(firstSection);
  }

  @Test
  void 모든_역_상행역부터_하행역_순으로_조회() {
    addSection(secondSection);

    List<Station> result = sections.getAllStation();

    assertThat(result).containsExactly(donongStation, ducksoStation, gooriStation);
  }

  @Test
  void 구간_등록_길이가_같으면_에러() {
    secondSection = new Section(line, donongStation, ducksoStation, 10);

    assertThatThrownBy(() -> sections.addSection(secondSection)).isInstanceOf(CustomException.class);
  }

  @Test
  void 구간_등록_길이가_크면_에러() {
    secondSection = new Section(line, donongStation, ducksoStation, 15);

    assertThatThrownBy(() -> sections.addSection(secondSection)).isInstanceOf(CustomException.class);
  }

  @Test
  void 구간_등록_상행_하행역이_모두_같으면_에러() {
    secondSection = new Section(line, donongStation, gooriStation, 6);

    assertThatThrownBy(() -> sections.addSection(secondSection)).isInstanceOf(CustomException.class);
  }

  @Test
  void 지하철_노선_등록할_구간_상행역_하행역_모두_없는_경우_에러() {
    Station jongrangStation = new Station("중랑역");
    secondSection = new Section(line, jongrangStation, ducksoStation, 6);

    assertThatThrownBy(() -> sections.addSection(secondSection)).isInstanceOf(CustomException.class);
  }

  @Test
  void 지하철_노선_중간_구간_제거() {
    addSection(secondSection);

    sections.removeSection(ducksoStation);

    List<Station> result = sections.getAllStation();

    assertThat(result).containsExactly(donongStation, gooriStation);
    assertThat(sections.getSections().get(0).getDistance()).isEqualTo(10);
  }

  @Test
  void 지하철_노선_상행역_제거() {
    addSection(secondSection);

    sections.removeSection(donongStation);

    List<Station> result = sections.getAllStation();

    assertThat(result).containsExactly(ducksoStation, gooriStation);
    assertThat(sections.getSections().get(0).getDistance()).isEqualTo(5);
  }

  @Test
  void 지하철_노선_구간_제거시_구간이_하나면_에러() {
    assertThatThrownBy(() -> sections.removeSection(donongStation)).isInstanceOf(CustomException.class);
  }

  @Test
  void 지하철_노선_구간_제거시_없는_역_제거의_경우_에러() {
    assertThatThrownBy(() -> sections.removeSection(ducksoStation)).isInstanceOf(CustomException.class);
  }

  private void addSection(Section section) {
    sections.addSection(section);
    assertThat(sections.getSections().get(0).getDistance()).isEqualTo(5);
  }
}