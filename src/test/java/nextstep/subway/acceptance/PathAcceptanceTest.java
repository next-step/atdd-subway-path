package nextstep.subway.acceptance;

import static nextstep.subway.utils.LineTestRequests.지하철_노선도_등록_응답값반환;
import static nextstep.subway.utils.SectionTestRequests.지하철_구간_등록;
import static nextstep.subway.utils.StationTestRequests.지하철_역_등록_Id_획득;

import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.utils.DBCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("지하철 경로 찾기 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PathAcceptanceTest {

  @Autowired
  private DBCleanup dbCleanup;
  private Long 노원역;
  private Long 하계역;
  private Long 판교역;
  private Long 논현역;
  private Long 정자역;
  private Long 서현역;
  private Long 수내역;

  @BeforeEach
  void init() {
    dbCleanup.execute();
    노원역 = 지하철_역_등록_Id_획득("노원역");
    하계역 = 지하철_역_등록_Id_획득("하계역");
    논현역 = 지하철_역_등록_Id_획득("논현역");
    판교역 = 지하철_역_등록_Id_획득("판교역");
    정자역 = 지하철_역_등록_Id_획득("정자역");
    서현역 = 지하철_역_등록_Id_획득("서현역");
    수내역 = 지하철_역_등록_Id_획득("수내역");

    // 7호선
    LineResponse seven = 지하철_노선도_등록_응답값반환("7호선", "갈색", 노원역, 논현역, 5);
    지하철_구간_등록(seven.getId(), 노원역, 하계역, 3);

    // 신분당선
    LineResponse newBundang = 지하철_노선도_등록_응답값반환("신분당선", "빨간색", 논현역, 정자역, 10);
    지하철_구간_등록(newBundang.getId(), 논현역, 판교역, 3);

    // 수인분당선
    LineResponse bundang = 지하철_노선도_등록_응답값반환("분당선", "노란색", 정자역, 서현역, 10);
    지하철_구간_등록(bundang.getId(), 정자역, 수내역,5);
  }

  /**
   * When  경로 검색을 했을 때에
   * Then  관련된 최단 거리 및 경로를 리턴한다.
   */
  @DisplayName("최단 경로 반환 기능")
  @Test
  void searchPath() {

  }


}
