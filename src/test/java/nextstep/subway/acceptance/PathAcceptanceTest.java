package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSectionAcceptanceTest.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청_후_ID_반환;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청_후_ID_반환;

class PathAcceptanceTest extends AcceptanceTest {

  private Long 교대역;
  private Long 강남역;
  private Long 양재역;
  private Long 남부터미널역;
  private Long 이호선;
  private Long 신분당선;
  private Long 삼호선;

  /**
   * 교대역    --- *2호선* ---   강남역
   * |                        |
   * *3호선*                   *신분당선*
   * |                        |
   * 남부터미널역  --- *3호선* ---   양재
   * Given 여러 노선을 생성하고 각 노선들에 구간들을 추가한 후
   */
  @BeforeEach
  public void setUp() {
    super.setUp();

    교대역 = 지하철역_생성_요청_후_ID_반환("교대역");
    강남역 = 지하철역_생성_요청_후_ID_반환("강남역");
    양재역 = 지하철역_생성_요청_후_ID_반환("양재역");
    남부터미널역 = 지하철역_생성_요청_후_ID_반환("남부터미널역");

    이호선 = 지하철_노선_생성_요청_후_ID_반환("2호선", "green", 교대역, 강남역, 10);
    신분당선 = 지하철_노선_생성_요청_후_ID_반환("신분당선", "red", 강남역, 양재역, 10);
    삼호선 = 지하철_노선_생성_요청_후_ID_반환("3호선", "orange", 교대역, 남부터미널역, 2);

    지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
  }

  /*
   * When 등록된, 연결된 A, B 역 사이의 최단 거리를 조회하면
   * Then A -> B의 최단 거리를 반환한다.
   */
  @Test
  @DisplayName("두 역 사이의 최단 거리 조회(성공)")
  void successTest() {
    // when
    ExtractableResponse<Response> searchResponse = 최단거리_검색(교대역, 양재역);

    // then
    상태_조회_성공_확인(searchResponse);
  }

  /*
   * When 연결되지 않은 A, B 역 사이의 최단 거리를 조회하면
   * Then 조회가 실패한다.
   */
  @Test
  @DisplayName("이어지지 않은 두 역 사이의 최단 거리 조회(실패)")
  void searchNonRelatedStationsTest() {
    // given
    Long 인덕원역 = 지하철역_생성_요청_후_ID_반환("인덕원역");
    Long 정부과천청사역 = 지하철역_생성_요청_후_ID_반환("정부과천청사역");
    Long 사호선 = 지하철_노선_생성_요청_후_ID_반환("4호선", "blue", 인덕원역, 정부과천청사역, 25);

    // when
    ExtractableResponse<Response> searchResponse = 최단거리_검색(남부터미널역, 인덕원역);

    // then
    상태_조회_잘못된_요청_확인(searchResponse);
  }

  /*
   * When 등록되지 않은 A, B역 사이의 거리를 조회할 경우
   * Then 조회가 실패한다.
   */
  @Test
  @DisplayName("등록되지 않은 두 역 사이의 최단 거리 조회(실패)")
  void searchUnregisteredStationsTest() {
    // given
    Long 인덕원역 = 지하철역_생성_요청_후_ID_반환("인덕원역");
    Long 정부과천청사역 = 지하철역_생성_요청_후_ID_반환("정부과천청사역");

    // when
    ExtractableResponse<Response> searchResponse = 최단거리_검색(정부과천청사역, 인덕원역);

    // then
    상태_조회_잘못된_요청_확인(searchResponse);
  }

  /*
   * When 동일한 A, A역 사이의 거리를 조회할 경우
   * Then 조회가 실패한다.
   */
  @Test
  @DisplayName("동일한 역 사이의 최단 거리 조회(실패)")
  void failedWithSameStationTest() {
    // when
    ExtractableResponse<Response> searchResponse = 최단거리_검색(교대역, 교대역);

    // then
    상태_조회_잘못된_요청_확인(searchResponse);
  }
}
