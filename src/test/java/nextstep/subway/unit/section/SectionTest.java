package nextstep.subway.unit.section;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionStations;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

class SectionTest {
  private final Station a = new Station("a");
  private final Station b = new Station("b");
  private final Station c = new Station("c");
  private final Station d = new Station("d");

  @Test
  void insertDownwardInterStation() {
    Section sectionAB = new Section(new SectionStations(a, b), 10);
    Section sectionAC = new Section(new SectionStations(a, c), 2);

    sectionAB.insertDownwardInterStation(sectionAC);

    assertThat(sectionAB.getUpwardStation()).isEqualTo(a);
    assertThat(sectionAB.getDownwardStation()).isEqualTo(c);
    assertThat(sectionAB.getDistance()).isEqualTo(2);

    assertThat(sectionAC.getUpwardStation()).isEqualTo(c);
    assertThat(sectionAC.getDownwardStation()).isEqualTo(b);
    assertThat(sectionAC.getDistance()).isEqualTo(8);
  }


  @Test
  void insertUpwardInterStation() {
    Section sectionAB = new Section(new SectionStations(a, b), 10);
    Section sectionCB = new Section(new SectionStations(c, b), 2);

    sectionAB.insertUpwardInterStation(sectionCB);

    assertThat(sectionAB.getUpwardStation()).isEqualTo(a);
    assertThat(sectionAB.getDownwardStation()).isEqualTo(c);
    assertThat(sectionAB.getDistance()).isEqualTo(8);

    assertThat(sectionCB.getUpwardStation()).isEqualTo(c);
    assertThat(sectionCB.getDownwardStation()).isEqualTo(b);
    assertThat(sectionCB.getDistance()).isEqualTo(2);
  }
}
