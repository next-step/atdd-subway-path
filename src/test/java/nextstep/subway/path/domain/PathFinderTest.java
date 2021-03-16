package nextstep.subway.path.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import nextstep.subway.line.acceptance.LineColor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;

public class PathFinderTest {

  private PathFinder pathFinder;
  private Station 광교역;
  private Station 광교중앙역;
  private Station 수지구청역;
  private Station 성복역;
  private Station 상현역;
  private Station 미금역;
  private Station 동천역;
  private Station 강남역;
  private Station 역삼역;
  private Station 사당역;
  private Line 신분당선;
  private Line 이호선;


  @BeforeEach
  void init(){
    역생성();
    노선생성();
    구간추가();
    pathFinder = new PathFinder();
  }

  void 역생성() {
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

    강남역 = new Station("강남역");
    ReflectionTestUtils.setField(강남역, "id", 9L);

    역삼역 = new Station("역삼역");
    ReflectionTestUtils.setField(역삼역, "id", 10L);

    사당역 = new Station("사당역");
    ReflectionTestUtils.setField(사당역, "id", 11L);
  }

  void 노선생성() {
    신분당선 = new Line("신분당선", LineColor.RED.toString(), 광교역, 광교중앙역, 5);
    이호선 = new Line("신분당선", LineColor.GREEN.toString(), 사당역, 역삼역, 5);
  }

  void 구간추가(){
    신분당선.addSection(광교중앙역, 상현역, 5);
    신분당선.addSection(상현역, 성복역, 5);
    신분당선.addSection(성복역, 수지구청역, 5);
    신분당선.addSection(수지구청역, 동천역, 5);
    신분당선.addSection(동천역, 미금역, 5);
    신분당선.addSection(미금역, 강남역, 5);

    이호선.addSection(강남역,역삼역,4);
  }


  @DisplayName("두 역간의 경로를 탐색한다")
  @Test
  void findPath(){
    //given
    Sections 신분당선_구간 = 신분당선.getSections();
    Sections 이호선_구간 =이호선.getSections();
    //when
    StationPath stationPath = pathFinder.findPath(Arrays.asList(신분당선_구간,이호선_구간),광교역,역삼역);
    //then
    int totalDistance = 39;
    assertThat(stationPath.getStations()).containsExactly(광교역,광교중앙역,상현역,성복역,수지구청역,동천역,미금역,강남역,역삼역);
    assertThat(stationPath.getDistance()).isEqualTo(totalDistance);
  }
}
