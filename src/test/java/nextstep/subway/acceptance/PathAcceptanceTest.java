package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.common.exception.message.PathErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

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

    이호선 = 지하철_노선_생성_요청(라인_파라미터_생성("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
    신분당선 = 지하철_노선_생성_요청(라인_파라미터_생성("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
    삼호선 = 지하철_노선_생성_요청(라인_파라미터_생성("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

    지하철_노선에_지하철_구간_생성_요청(삼호선, 구간_파라미터_생성(남부터미널역, 양재역, 3));
  }

  @Test
  void 최단_경로_조회() {
    ExtractableResponse<Response> result = 최단_경로_조회(교대역, 양재역);

    assertThat(result.jsonPath().getList("stations.id", Long.class)).hasSize(3).containsExactly(교대역, 남부터미널역, 양재역);
    assertThat(result.jsonPath().getLong("distance")).isEqualTo(5);
  }

  @Test
  void 최단_경로_출발역이_없으면_에러() {
    Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

    ExtractableResponse<Response> result = 최단_경로_조회(판교역, 양재역);

    assertThat(result.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(result.jsonPath().getString("message")).isEqualTo(PathErrorMessage.PATH_STATION_EMPTY);
  }

  @Test
  void 최단_경로_종착역이_없으면_에러() {
    Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

    ExtractableResponse<Response> result = 최단_경로_조회(교대역, 판교역);

    assertThat(result.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(result.jsonPath().getString("message")).isEqualTo(PathErrorMessage.PATH_STATION_EMPTY);
  }

  @Test
  void 최단_경로_동일한_역을_조회하면_에러() {
    ExtractableResponse<Response> result = 최단_경로_조회(교대역, 교대역);

    assertThat(result.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    assertThat(result.jsonPath().getString("message")).isEqualTo(PathErrorMessage.STATION_DUPLICATE);
  }

  private ExtractableResponse<Response> 최단_경로_조회(Long sourceId, Long targetId) {
    return RestAssured.given().log().all()
        .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
        .then().log().all()
        .extract();
  }

  private Map<String, String> 라인_파라미터_생성(String name, String color, Long upStationId, Long downStationId, int distance) {
    Map<String, String> lineCreateParams;
    lineCreateParams = new HashMap<>();
    lineCreateParams.put("name", name);
    lineCreateParams.put("color", color);
    lineCreateParams.put("upStationId", upStationId + "");
    lineCreateParams.put("downStationId", downStationId + "");
    lineCreateParams.put("distance", distance + "");
    return lineCreateParams;
  }

  private Map<String, String> 구간_파라미터_생성(Long upStationId, Long downStationId, int distance) {
    Map<String, String> params = new HashMap<>();
    params.put("upStationId", upStationId + "");
    params.put("downStationId", downStationId + "");
    params.put("distance", distance + "");
    return params;
  }
}
