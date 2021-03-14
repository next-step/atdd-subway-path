package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


import java.util.List;
import nextstep.subway.common.exception.InvalidSectionException;
import nextstep.subway.line.acceptance.LineColor;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class LineTest {

  private Station 광교역;
  private Station 광교중앙역;
  private Line 신분당선;

  @BeforeEach
  void init() {
    광교역 = mock(Station.class);
    광교중앙역 = mock(Station.class);
    given(광교역.getName()).willReturn("광교역");
    given(광교역.getId()).willReturn(1L);
    given(광교중앙역.getName()).willReturn("광교중앙역");
    given(광교중앙역.getId()).willReturn(2L);
    신분당선 = new Line("신분당선", LineColor.RED.toString(), 광교역, 광교중앙역, 5);
  }

  @DisplayName("노선에 포함된 역을 조회")
  @Test
  void getSections() {
    List<Station> 신분당선_역목록 = 신분당선.getSections().getSortedStations();
    assertThat(신분당선_역목록).extracting(Station::getName).containsExactly("광교역", "광교중앙역");
  }

  @DisplayName("노선에 구간을 추가")
  @Test
  void addSection() {
    Station 상현역 = new Station("상현역");
    신분당선.addSection(광교중앙역, 상현역, 5);
    assertThat(신분당선.getSections().getSortedStations())
        .extracting(Station::getName).containsExactly("광교역", "광교중앙역", "상현역");
  }

  @DisplayName("추가하는 구간이 이미 등록되어있으면 Exception")
  @Test
  void addSectionWithDuplicatedSection() {
    //given
    Station 상현역 = new Station("상현역");
    신분당선.addSection(광교중앙역, 상현역, 5);
    //then
    assertThrows(InvalidSectionException.class, () -> {
      신분당선.addSection(상현역, 광교중앙역, 5);
    });

  }

  @DisplayName("추가하는 구간의 상행역이나 하행역이 노선에 포함되어있으면 true")
  @Test
  void isRegisteredStationTest(){
    boolean result = 신분당선.getSections().isRegisteredStation(광교역);
    assertThat(true).isEqualTo(result);
  }

  @DisplayName("추가하는 구간의 상행역이나 하행역이 노선에 포함되어있지않으면 false")
  @Test
  void isNotRegisteredStationTest(){
    //given
    Station 수지구청역 = mock(Station.class);
    given(수지구청역.getName()).willReturn("수지구청역");
    given(수지구청역.getId()).willReturn(6L);
    //when
    boolean result = 신분당선.getSections().isRegisteredStation(수지구청역);
    //then
    assertThat(false).isEqualTo(result);
  }

  @DisplayName("노선 중간에 역을 삽입하면 원래 구간의 길이에서 새로운 구간의 길이를 뺀값이다.")
  @Test
  void calculateNewDistance(){
    //given
    Station 수지구청역 = mock(Station.class);
    Station 성복역 = mock(Station.class);
    Station 상현역 = mock(Station.class);
    given(수지구청역.getName()).willReturn("수지구청역");
    given(수지구청역.getId()).willReturn(6L);
    given(성복역.getName()).willReturn("성복역");
    given(성복역.getId()).willReturn(5L);
    given(상현역.getName()).willReturn("상현역");
    given(상현역.getId()).willReturn(4L);
    신분당선.addSection(광교중앙역, 상현역, 5);
    신분당선.addSection(상현역,수지구청역,7);
    //when
    신분당선.addSection(상현역,성복역,3);
    List<Section> 신분당선_구간 = 신분당선.getSections().getSortedSection();
    //then
    assertThat(신분당선_구간.get(신분당선_구간.size() - 1).getDistance()).isEqualTo(4);
  }

  @DisplayName("노선의 중간에 역을 삽입한다")
  @Test
  void insertSectionToSections(){
    //given
    Station 수지구청역 = mock(Station.class);
    Station 성복역 = mock(Station.class);
    Station 상현역 = mock(Station.class);
    Station 미금역 = mock(Station.class);
    Station 동천역 = mock(Station.class);
    given(수지구청역.getName()).willReturn("수지구청역");
    given(수지구청역.getId()).willReturn(6L);
    given(성복역.getName()).willReturn("성복역");
    given(성복역.getId()).willReturn(5L);
    given(상현역.getName()).willReturn("상현역");
    given(상현역.getId()).willReturn(4L);
    given(미금역.getName()).willReturn("미금역");
    given(미금역.getId()).willReturn(7L);
    given(동천역.getName()).willReturn("동천역");
    given(동천역.getId()).willReturn(8L);
    신분당선.addSection(광교중앙역, 상현역, 5);
    신분당선.addSection(상현역,수지구청역,3);
    신분당선.addSection(상현역,성복역,2);
    List<Section> 신분당선_구간 = 신분당선.getSections().getSortedSection();
    신분당선.addSection(수지구청역,미금역,5);
    //when
    신분당선.addSection(동천역,미금역,3);
    //then
    assertThat(신분당선.getSections().getSortedStations())
        .extracting(Station::getName).containsExactly("광교역", "광교중앙역", "상현역","성복역","수지구청역","동천역","미금역");
  }

  @DisplayName("노선의 중간에 기존 구간의 길이보다 길이가 같거나 긴 역을 삽입하면 Exception")
  @Test
  void insertSectionToSectionsWithExceedLength(){
    //given
    Station 수지구청역 = mock(Station.class);
    Station 성복역 = mock(Station.class);
    Station 상현역 = mock(Station.class);
    Station 미금역 = mock(Station.class);
    Station 동천역 = mock(Station.class);
    given(수지구청역.getName()).willReturn("수지구청역");
    given(수지구청역.getId()).willReturn(6L);
    given(성복역.getName()).willReturn("성복역");
    given(성복역.getId()).willReturn(5L);
    given(상현역.getName()).willReturn("상현역");
    given(상현역.getId()).willReturn(4L);
    given(미금역.getName()).willReturn("미금역");
    given(미금역.getId()).willReturn(7L);
    given(동천역.getName()).willReturn("동천역");
    given(동천역.getId()).willReturn(8L);
    신분당선.addSection(광교중앙역, 상현역, 5);
    신분당선.addSection(상현역,수지구청역,3);
    신분당선.addSection(상현역,성복역,1);
    신분당선.addSection(수지구청역,미금역,5);

    //when then
    assertThrows(InvalidSectionException.class, ()-> {
      신분당선.addSection(동천역,미금역,5);
    });
  }

  @DisplayName("노선에 역을 삭제")
  @Test
  void removeSection() {
    //given
    Station 상현역 = Mockito.mock(Station.class);
    given(상현역.getId()).willReturn(3L);
    신분당선.addSection(광교중앙역, 상현역, 5);
    //when
    신분당선.removeSection(상현역.getId());
    //then
    assertThat(신분당선.getSections().getSortedStations())
        .extracting(Station::getName).containsExactly("광교역", "광교중앙역");
  }

  @DisplayName("노선의 종점이 아닌역을 삭제하면 Exception")
  @Test
  void removeSectionWithoutLastSection() {
    //given
    Station 상현역 = Mockito.mock(Station.class);
    given(상현역.getId()).willReturn(3L);
    신분당선.addSection(광교중앙역, 상현역, 5);

    //then
    assertThrows(InvalidSectionException.class, () -> {
      신분당선.removeSection(광교중앙역.getId());
    });
  }

  @DisplayName("노선의 구간이 1개만 있을때 삭제하면 Exception")
  @Test
  void removeSectionIncludedOneSection() {
    assertThrows(InvalidSectionException.class, () -> {
      신분당선.removeSection(광교중앙역.getId());
    });
  }
}
