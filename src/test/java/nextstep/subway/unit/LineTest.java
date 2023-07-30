package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LineTest {

  private Station 첫번째역;
  private Station 두번째역;
  private Station 세번째역;
  private Station 네번째역;
  private Line 첫번째노선;

  @BeforeEach
  public void setup() {
    첫번째역 = new Station(1L, "첫번째역");
    두번째역 = new Station(2L, "두번째역");
    세번째역 = new Station(3L, "세번째역");
    네번째역 = new Station(4L, "네번째역");
    첫번째노선 = Line.of("첫번째노선", "BLUE", 첫번째역, 두번째역, 10L);
  }

  @DisplayName("도메인(Line): 노선에 속해있는 역들에 대한 조회")
  @Test
  void getStations() {
    //When
    첫번째노선.addSection(Section.of(첫번째노선, 10L, 두번째역, 세번째역));
    //Then
    assertThat(첫번째노선.getStations()).containsExactly(첫번째역, 두번째역, 세번째역);
  }

  @Nested
  @DisplayName("성공 케이스: 도메인 (Line): 새로운 구간 추가할 때")
  class SuccessfulSectionAddingTest {

    @DisplayName("도메인(Line): 새로운 구간을 추가")
    @Test
    void addSection() {
      //When
      첫번째노선.addSection(Section.of(첫번째노선, 10L, 두번째역, 세번째역));
      Section section = 첫번째노선.getLastSection();
      //Then
      assertThat(section.getDistance()).isEqualTo(10L);
      assertThat(section.getUpStation()).isEqualTo(두번째역);
      assertThat(section.getDownStation()).isEqualTo(세번째역);
    }

    @DisplayName("존재하는 구간중 상행역에 구간을 추가합니다. (기존 구간보다 추가하는 거리가 적을 때)")
    @Test
    void addSectionOnUpStation() {
      //When
      첫번째노선.addSection(Section.of(첫번째노선, 4L, 세번째역, 두번째역));
      Section section = 첫번째노선.getLastSection();

      //Then
      assertThat(section.getDistance()).isEqualTo(4L);
      assertThat(section.getUpStation()).isEqualTo(세번째역);
      assertThat(section.getDownStation()).isEqualTo(두번째역);
      assertThat(첫번째노선.getStations().stream().map(station -> station.getName()).collect(
          Collectors.toList())).containsExactly("첫번째역","세번째역","두번째역");
    }

    @DisplayName("존재하는 구간중 하행역에 구간을 추가합니다. (기존 구간보다 추가하는 거리가 적을 때)")
    @Test
    void addSectionOnDownStation() {
      //When
      첫번째노선.addSection(Section.of(첫번째노선, 4L, 두번째역, 세번째역));
      Section section = 첫번째노선.getLastSection();

      //Then
      assertThat(section.getDistance()).isEqualTo(4L);
      assertThat(section.getUpStation()).isEqualTo(두번째역);
      assertThat(section.getDownStation()).isEqualTo(세번째역);
      assertThat(첫번째노선.getStations().stream().map(station -> station.getName()).collect(
      Collectors.toList())).containsExactly("첫번째역","두번째역","세번째역");
    }
  }

  @Nested
  @DisplayName("오류 케이스: 도메인 (Line): 새로운 구간 추가할 때")
  class FailedSectionAddingTest {

    @DisplayName("존재하는 구간중 상행역에 구간을 추가합니다. (기존 구간보다 추가하는 거리가 클 때)")
    @Test
    void addSectionOnUpStationInvalidDistance() {
      //When
      Throwable thrown = catchThrowable(() -> { 첫번째노선.addSection(Section.of(첫번째노선, 10L, 첫번째역, 두번째역)); });
      //Then
      assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하는 구간중 하행역에 구간을 추가합니다. (기존 구간보다 추가하는 거리가 클 때)")
    @Test
    void addSectionInvalidDistance() {
      //When
      Throwable thrown = catchThrowable(() -> { 첫번째노선.addSection(Section.of(첫번째노선, 12L, 세번째역, 두번째역)); });
      //Then
      assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하는 구간중 상행역에 구간을 추가합니다. (기존 동일 구간이 존재할 때)")
    @Test
    void addSectionAlreadyExistingSection() {
      //When
      Throwable thrown = catchThrowable(() -> { 첫번째노선.addSection(Section.of(첫번째노선, 5L, 첫번째역, 두번째역)); });
      //Then
      assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하는 구간중 하행역에 구간을 추가합니다. (추가하는 구간의 역이 존재하지 않을 때)")
    @Test
    void addSectionWithNotExistingStations() {
      //When
      Throwable thrown = catchThrowable(() -> { 첫번째노선.addSection(Section.of(첫번째노선, 10L, 세번째역, 네번째역)); });
      //Then
      assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }
  }
  @Nested
  @DisplayName("도메인 (Line): 구간 삭제할 때")
  class SectionRemovingTest {
    @DisplayName("도메인(Line): 구간의 마지막 역을 삭제")
    @Test
    void removeSection() {
      //Given
      첫번째노선.addSection(Section.of(첫번째노선, 10L, 두번째역, 세번째역));
      //When
      첫번째노선.deleteSection(세번째역);
      //Then
      assertThat(첫번째노선.getStations().stream().map(station -> station.getName())).containsExactly(
          첫번째역.getName(), 두번째역.getName());
    }

    @DisplayName("도메인(Line): 구간의 중간 역을 삭제")
    @Test
    void removeMiddleSection() {
      //Given
      첫번째노선.addSection(Section.of(첫번째노선, 10L, 두번째역, 세번째역));
      //When
      첫번째노선.deleteSection(두번째역);
      //Then
      assertThat(첫번째노선.getStations()
          .stream().map(station -> station.getName()))
          .containsExactly(첫번째역.getName(), 세번째역.getName());
    }

    @DisplayName("오류 케이스: 구간이 한개인 노선의 하행역에 구간을 삭제합니다. (노선에 구간이 하나 일 떄")
    @Test
    void addSectionWithNotExistingStations() {
      //When
      Throwable thrown = catchThrowable(() -> { 첫번째노선.deleteSection(두번째역); });
      //Then
      assertThat(thrown).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("오류 케이스: 하행역에 구간을 삭제합니다. (삭제하는 구간의 역이 존재하지 않을 때)")
    @Test
    void removeSectionWithNotExistingStations() {
      //When
      Throwable thrown = catchThrowable(() -> { 첫번째노선.deleteSection(네번째역); });
      //Then
      assertThat(thrown).isInstanceOf(IllegalStateException.class);
    }
  }

}


