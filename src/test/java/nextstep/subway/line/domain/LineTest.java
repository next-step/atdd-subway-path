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

  @DisplayName("추가하는 구간의 상행역이 노선의 종점역이 아니면 Exception")
  @Test
  void addSectionWithoutLastSection() {
    //given
    Station 상현역 = new Station("상현역");
    Station 성복역 = new Station("성복역");
    //then
    assertThrows(InvalidSectionException.class, () -> {
      신분당선.addSection(상현역, 성복역, 5);
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
