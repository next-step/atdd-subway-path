package nextstep.subway.path.application;


import static org.junit.jupiter.api.Assertions.assertThrows;
import nextstep.subway.common.exception.InvalidStationPathException;
import nextstep.subway.line.acceptance.LineColor;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;

import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("역 탐색 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class PathServiceTest {

  @Mock
  private StationService stationService;
  @Mock
  private LineService lineService;
  private PathService pathService;

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
    pathService = new PathService(lineService, stationService);
    역생성();
    노선생성();
    구간추가();

  }

  void 역생성() {
    광명역 = new Station("광명역");
    ReflectionTestUtils.setField(광명역, "id", 12L);

    금천구청역 = new Station("금천구청역");
    ReflectionTestUtils.setField(금천구청역, "id", 13L);

    독산역 = new Station("독산역");
    ReflectionTestUtils.setField(독산역, "id", 14L);

    철산역 = new Station("철산역");
    ReflectionTestUtils.setField(철산역, "id", 15L);

    역삼역 = new Station("역삼역");
    ReflectionTestUtils.setField(역삼역, "id", 10L);

    사당역 = new Station("사당역");
    ReflectionTestUtils.setField(사당역, "id", 11L);

    강남역 = new Station("강남역");
    ReflectionTestUtils.setField(강남역, "id", 9L);

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

  }

  void 노선생성() {
    일호선 = new Line("일호선", LineColor.BLUE.toString(), 광명역, 독산역, 5);
    ReflectionTestUtils.setField(일호선, "id", 3L);
    이호선 = new Line("신분당선", LineColor.GREEN.toString(), 사당역, 강남역, 5);
    ReflectionTestUtils.setField(이호선, "id", 2L);
    신분당선 = new Line("신분당선", LineColor.RED.toString(), 광교역, 광교중앙역, 5);
    ReflectionTestUtils.setField(신분당선, "id", 1L);
  }

  void 구간추가() {
    일호선.addSection(독산역, 금천구청역, 4);
    이호선.addSection(강남역, 역삼역, 4);
    신분당선.addSection(광교중앙역, 상현역, 5);
    신분당선.addSection(상현역, 성복역, 5);
    신분당선.addSection(성복역, 수지구청역, 5);
    신분당선.addSection(수지구청역, 동천역, 5);
    신분당선.addSection(동천역, 미금역, 5);
    신분당선.addSection(미금역, 강남역, 5);
  }

  @DisplayName("시작역 도착역이 같으면 경로찾기를 실패한다")
  @Test
  void searchPathTestWithException() {
    //when then
    assertThrows(InvalidStationPathException.class, () -> {
      pathService.findPath(광교역.getId(), 광교역.getId());
    });
  }
}
