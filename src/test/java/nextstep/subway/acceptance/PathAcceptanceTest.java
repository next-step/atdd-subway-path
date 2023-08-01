package nextstep.subway.acceptance;

import static nextstep.subway.utils.LineTestRequests.지하철_노선도_등록_응답값반환;
import static nextstep.subway.utils.PathTestRequests.지하철_경로_조회;
import static nextstep.subway.utils.PathTestRequests.지하철_경로_조회_응답값_조회;
import static nextstep.subway.utils.SectionTestRequests.지하철_구간_등록;
import static nextstep.subway.utils.StationTestRequests.지하철_역_등록_Id_획득;
import static nextstep.subway.utils.StatusCodeAssertions.에러코드_검증;
import static nextstep.subway.utils.StatusCodeAssertions.응답코드_검증;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.path.controller.dto.PathResponse;
import nextstep.subway.station.controller.dto.StationResponse;
import nextstep.subway.utils.DBCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

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
  private Long 가짜역;

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
    가짜역 = 지하철_역_등록_Id_획득("가짜역");

    // 7호선
    LineResponse seven = 지하철_노선도_등록_응답값반환("7호선", "갈색", 노원역, 논현역, 5);
    지하철_구간_등록(seven.getId(), 노원역, 하계역, 3);
    지하철_구간_등록(seven.getId(), 논현역, 서현역, 300);


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
  @DisplayName("최단 길이 경로 반환 기능")
  @Test
  void searchPath() {
    //when
    ExtractableResponse<Response> result = 지하철_경로_조회(노원역, 서현역);
    PathResponse response = 지하철_경로_조회_응답값_조회(result);

    // then 노원 - 하계 - 논현 - 판교 - 정자 - 수내 - 서현
    응답코드_검증(result, HttpStatus.OK);
    경로_순서_기대값_검증(response, Arrays.asList(노원역, 하계역, 논현역, 판교역, 정자역, 수내역, 서현역));
    경로_길이_기대값_검증(response, 25d);
  }

  /**
   * When  경로 검색을 했을 때에 시작, 출발이 같을 때에
   * Then  에러 상태 값을 리턴한다
   */
  @DisplayName("경로 검색을 했을 때에 시작, 출발이 같을 때에 옳지 않은 파라미터 에러를 반환한다")
  @Test
  void searchPathThrowExceptionSameSourceTarget() {
    //when
    ExtractableResponse<Response> result = 지하철_경로_조회(노원역, 노원역);

    //then
    응답코드_검증(result, HttpStatus.BAD_REQUEST);
    에러코드_검증(result, ErrorCode.INVALID_PARAM);
  }

  /**
   * When  경로 검색을 했을 때에 출발역과 도착역이 연결되지 않았을 때에
   * Then  에러 상태 값을 리턴한다
   */
  @DisplayName("경로 검색을 했을 때에 시작, 출발역과 도착역이 연결되지 않았을 때에 에러를 반환한다")
  @Test
  void searchPathThrowExceptionNotConnected() {
    //when
    ExtractableResponse<Response> result = 지하철_경로_조회(가짜역, 서현역);

    //then
    응답코드_검증(result, HttpStatus.BAD_REQUEST);
    에러코드_검증(result, ErrorCode.STATIONS_ARE_NOT_CONNECTED);
  }

  /**
   * When  경로 검색을 했을 때에 없는 역을 검색했을 때에
   * Then  에러 상태 값을 리턴한다
   */
  @DisplayName("경로 검색을 했을 때에 시작, 없는 역을 검색했을 때에 때에 에러를 반환한다")
  @Test
  void searchPathThrowExceptionNotFoundStation() {
    //when
    ExtractableResponse<Response> result = 지하철_경로_조회(-1L, 서현역);

    //then
    응답코드_검증(result, HttpStatus.NOT_FOUND);
  }


  private void 경로_길이_기대값_검증(PathResponse response, Double distance) {
    Double resultDistance = response.getDistance();
    assertThat(resultDistance).isEqualTo(distance);
  }

  private void 경로_순서_기대값_검증(PathResponse response, List<Long> stationIds) {
    List<StationResponse> stationResponses = response.getStations();
    List<Long> responseStationIds = stationResponses
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

    assertThat(responseStationIds).containsExactly(stationIds.toArray(Long[]::new));
  }
}
