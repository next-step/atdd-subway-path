package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import nextstep.subway.common.exception.InvalidSectionException;
import nextstep.subway.line.acceptance.LineColor;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class LineTest {

  private Station 광교역;
  private Station 광교중앙역;
  private Station 수지구청역;
  private Station 성복역;
  private Station 상현역;
  private Station 미금역;
  private Station 동천역;
  private Line 신분당선;

  @BeforeEach
  void init() {

    광교역 = new Station("광교역");
    ReflectionTestUtils.setField(광교역, "id", 1L);

    광교중앙역 = new Station("광교중앙역");
    ReflectionTestUtils.setField(광교중앙역, "id", 2L);

    수지구청역 = new Station("수지구청역");
    ReflectionTestUtils.setField(수지구청역, "id", 4L);

    성복역 = new Station("성복역");
    ReflectionTestUtils.setField(성복역, "id", 5L);

    상현역 = new Station("상현역");
    ReflectionTestUtils.setField(상현역, "id", 6L);

    미금역 = new Station("미금역");
    ReflectionTestUtils.setField(미금역, "id", 7L);

    동천역 = new Station("동천역");
    ReflectionTestUtils.setField(동천역, "id", 8L);

    신분당선 = new Line("신분당선", LineColor.RED.toString(), 광교역, 광교중앙역, 5);

  }

  @DisplayName("노선에 포함된 역을 조회")
  @Test
  void getSections() {
    //when
    Sections 신분당선_구간목록 = 신분당선.getSections();
    //then
    assertThat(신분당선_구간목록.getSortedStations()).contains(광교역, 광교중앙역);
  }

  @DisplayName("노선에 구간을 추가")
  @Test
  void addSection() {
    //when
    신분당선.addSection(광교중앙역, 상현역, 5);
    //then
    assertThat(신분당선.getSections().getSortedStations()).contains(광교역, 광교중앙역);
  }

  @DisplayName("추가하는 구간이 이미 등록되어있으면 Exception")
  @Test
  void addSectionWithDuplicatedSection() {
    //given
    신분당선.addSection(광교중앙역, 상현역, 5);
    //when then
    assertThrows(InvalidSectionException.class, () -> {
      신분당선.addSection(상현역, 광교중앙역, 5);
    });

  }

  @DisplayName("추가하는 구간의 상행역이나 하행역이 노선에 포함되어있으면 true")
  @Test
  void isRegisteredStationTest() {
    //when
    boolean result = 신분당선.getSections().isRegisteredStation(광교역);
    //then
    assertThat(true).isEqualTo(result);
  }

  @DisplayName("추가하는 구간의 상행역이나 하행역이 노선에 포함되어있지않으면 false")
  @Test
  void isNotRegisteredStationTest() {
    //when
    boolean result = 신분당선.getSections().isRegisteredStation(수지구청역);
    //then
    assertThat(false).isEqualTo(result);
  }

  @DisplayName("노선 중간에 역을 삽입하면 원래 구간의 길이에서 새로운 구간의 길이를 뺀값이다.")
  @Test
  void calculateNewDistance() {
    //given
    신분당선.addSection(광교중앙역, 상현역, 5);
    신분당선.addSection(상현역, 수지구청역, 7);
    //when
    신분당선.addSection(상현역, 성복역, 3);
    List<Section> 신분당선_구간 = 신분당선.getSections().getSortedSection();
    //then
    assertThat(신분당선_구간.get(신분당선_구간.size() - 1).getDistance()).isEqualTo(4);
  }

  @DisplayName("노선의 중간에 역을 삽입한다")
  @Test
  void insertSectionToSections() {
    //given
    신분당선.addSection(광교중앙역, 상현역, 5);
    신분당선.addSection(상현역, 수지구청역, 3);
    신분당선.addSection(상현역, 성복역, 2);
    신분당선.addSection(수지구청역, 미금역, 5);
    //when
    신분당선.addSection(동천역, 미금역, 3);
    //then
    assertThat(신분당선.getSections().getSortedStations())
        .extracting(Station::getName)
        .containsExactly("광교역", "광교중앙역", "상현역", "성복역", "수지구청역", "동천역", "미금역");
  }

  @DisplayName("노선의 중간에 기존 구간의 길이보다 길이가 같거나 긴 역을 삽입하면 Exception")
  @Test
  void insertSectionToSectionsWithExceedLength() {
    //given
    신분당선.addSection(광교중앙역, 상현역, 5);
    신분당선.addSection(상현역, 수지구청역, 3);
    신분당선.addSection(상현역, 성복역, 1);
    신분당선.addSection(수지구청역, 미금역, 5);
    //when then
    assertThrows(InvalidSectionException.class, () -> {
      신분당선.addSection(동천역, 미금역, 5);
    });
  }

  @DisplayName("노선에 구간을 삭제")
  @Test
  void removeSection() {
    //given
    신분당선.addSection(광교중앙역, 상현역, 5);
    //when
    신분당선.removeSection(상현역.getId());
    //then
    assertThat(신분당선.getSections().getSortedStations()).contains(광교역, 광교중앙역);
  }

  @DisplayName("노선의 종점이 아닌역을 삭제하면 Exception")
  @Test
  void removeSectionWithoutLastSection() {
    //given
    신분당선.addSection(광교중앙역, 상현역, 5);
    //when then
    assertThrows(InvalidSectionException.class, () -> {
      신분당선.removeSection(광교중앙역.getId());
    });
  }

  @DisplayName("노선의 구간이 1개만 있을때 삭제하면 Exception")
  @Test
  void removeSectionIncludedOneSection() {
    //when then
    assertThrows(InvalidSectionException.class, () -> {
      신분당선.removeSection(광교중앙역.getId());
    });
  }
}
