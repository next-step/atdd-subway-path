package nextstep.subway.path.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import nextstep.subway.common.exception.InvalidStationPathException;
import nextstep.subway.line.acceptance.LineColor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.utils.LineHelper;
import nextstep.subway.utils.StationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
  private Station 광명역;
  private Station 금천구청역;
  private Station 독산역;
  private Station 철산역;
  private Line 신분당선;
  private Line 이호선;
  private Line 일호선;

  /**
   * [노선 미등록 역]
   *
   * 철산역
   *
   * [1호선]
   *     광명역-독산역교-금천구
   *
   * [2호선]
   *     사당-----강남----역삼청
   *              ㅣ
   *              ㅣ
   * [신분당]       ㅣ
   *              ㅣ
   *              ㅣ
   *              미금
   *              ㅣ
   *              동천
   *              ㅣ
   *              수지구청교
   *              ㅣ
   *              상현
   *              ㅣ
   *              성복
   *              ㅣ
   *              광교중앙
   *              ㅣ
   *              광교
   *
   *
   *
   */
  @BeforeEach
  void init() {
    일호선_생성();
    이호선_생성();
    신분당선_생성();
    pathFinder = PathFinder.of(Arrays.asList(신분당선.getSections(),이호선.getSections(),일호선.getSections()));
  }

  void 일호선_생성() {
    광명역 = StationHelper.createStation("광명역",12L);
    금천구청역 = StationHelper.createStation("금천구청역",13L);
    독산역 = StationHelper.createStation("독산역",14L);
    철산역 = StationHelper.createStation("철산역",15L);
    일호선 = LineHelper.createLine("일호선",LineColor.BLUE.toString(),광명역,금천구청역,4,3L);
    일호선.addSection(독산역, 금천구청역, 3);

  }

  void 이호선_생성() {
    역삼역 = StationHelper.createStation("역삼역",10L);
    사당역 = StationHelper.createStation("사당역",11L);
    강남역 = StationHelper.createStation("강남역",9L);
    이호선 = LineHelper.createLine("이호선",LineColor.GREEN.toString(), 사당역, 강남역, 5,3L);
    이호선.addSection(강남역,역삼역,4);

  }

  void 신분당선_생성() {
    광교역 = StationHelper.createStation("광교역",1L);
    광교중앙역 = StationHelper.createStation("광교중앙역",2L);
    수지구청역 = StationHelper.createStation("수지구청역",4L);
    성복역 = StationHelper.createStation("성복역",5L);
    상현역 =  StationHelper.createStation("상현역",6L);
    미금역 = StationHelper.createStation("미금역",7L);
    동천역 = StationHelper.createStation("동천역",8L);
    신분당선 = LineHelper.createLine("신분당선",LineColor.RED.toString(), 광교역, 광교중앙역, 5,1L);
    신분당선.addSection(광교중앙역, 상현역, 5);
    신분당선.addSection(상현역, 성복역, 5);
    신분당선.addSection(성복역, 수지구청역, 5);
    신분당선.addSection(수지구청역, 동천역, 5);
    신분당선.addSection(동천역, 미금역, 5);
    신분당선.addSection(미금역, 강남역, 5);

  }

  @DisplayName("두 역간의 경로를 탐색한다")
  @Test
  void findPath() {
    //when
    Path path = pathFinder.findPath(광교역, 역삼역);
    //then
    int totalDistance = 39;
    assertThat(path.getStations())
        .containsExactly(광교역, 광교중앙역, 상현역, 성복역, 수지구청역, 동천역, 미금역, 강남역, 역삼역);
    assertThat(path.getDistance()).isEqualTo(totalDistance);
  }

  @DisplayName("탐색하려는 경로의 출발역 도착역이 같으면 Exception")
  @Test
  void findPathWithSameStation() {
    //when then
    assertThrows(InvalidStationPathException.class, () -> {
      pathFinder.findPath(광교역, 광교역);
    });
  }

  @DisplayName("탐색하려는 경로의 출발역과 도착역이 연결되어있지 않으면 Exception")
  @Test
  void findPathWithNoConnectedStation() {
    //when then
    assertThrows(InvalidStationPathException.class, () -> {
      pathFinder.findPath(광교역, 광명역);
    });
  }

}
