package nextstep.subway.acceptance;

import org.junit.jupiter.api.BeforeEach;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청_후_ID_반환;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

import static nextstep.subway.acceptance.LineSectionAcceptanceTest.createSectionCreateParams;

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
   */
  @BeforeEach
  public void setUp() {
    super.setUp();

    교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
    강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
    양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
    남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

    이호선 = 지하철_노선_생성_요청_후_ID_반환("2호선", "green", 교대역, 강남역, 10);
    신분당선 = 지하철_노선_생성_요청_후_ID_반환("신분당선", "red", 강남역, 양재역, 10);
    삼호선 = 지하철_노선_생성_요청_후_ID_반환("3호선", "orange", 교대역, 남부터미널역, 2);

    지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
  }


}
