package nextstep.subway.acceptance;

import static nextstep.subway.steps.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.steps.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.steps.PathSteps.지하철_역_경로조회;
import static nextstep.subway.steps.PathSteps.지하철_역_경로조회_도착역_없음;
import static nextstep.subway.steps.StationSteps.지하철역_생성_요청;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.config.ManualCleanAcceptanceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.http.HttpStatus;

@DisplayName("경로조회 기능")
@TestInstance(Lifecycle.PER_CLASS)
public class PathAcceptanceTest extends ManualCleanAcceptanceTest {

  private static boolean 노선_초기화_완료 = false;

  // 2 - 4 호선 겹침
  private static Long 사당역;

  // 4 - 7 호선 겹침
  private static Long 총신대입구역;

  // 2호선
  private static Long _2호선;
  private static Long 봉천역;
  private static Long 서울대입구역;
  private static Long 낙성대역;

  // 7호선
  private static Long _7호선;
  private static Long 남성역;
  private static Long 숭실대입구역;

  // 4호선
  private static Long _4호선;
  private static Long 동작역;

  // 999호선 (다른 노선들과 연결 되어있지 않음)
  private static Long _999호선;
  private static Long _999_시작역;
  private static Long _999_종점역;

  /**
   * 노선도
   *
   * <2호선>
   * 봉천역 --3-- 서울대입구역 --4-- 낙성대역 --5-- 사당역
   *
   * <4호선>
   * 사당역 --5-- 총신대입구역 --5-- 동작역
   *
   * <7호선>
   * 총신대입구역 --5-- 남성역 --5-- 숭실대입구역
   *
   *
   * <999호선> (연결 X)
   * _999_시작역 --5-- _999_종점역
   */
  @BeforeAll
  public void setup() {
    사당역 = 지하철역_생성_요청("사당역").jsonPath().getLong("id");
    총신대입구역 = 지하철역_생성_요청("총신대입구역").jsonPath().getLong("id");
    _2호선_init();
    _4호선_init();
    _7호선_init();
    _999호선_init();
  }

  @AfterAll
  public void teardown() {
    super.databaseCleanup.execute();
  }

  /**
   * Given 하나 이상의 노선을 가진 임의의 지하철 노선도에서
   * When 출발역과 도착역에 대한 경로를 조회하면
   * Then 출발역부터 도착역까지의 최단거리와 지나는 역 정보들을 리턴한다.
   */
  @Test
  @DisplayName("출발역부터 도착역까지 최단경로 역과 거리를 조회한다.")
  void 경로조회_성공() {
    // when
    ExtractableResponse<Response> response = 지하철_역_경로조회(봉천역, 숭실대입구역);

    // then
    Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    Assertions.assertThat(response.jsonPath().getLong("distance")).isEqualTo(27L);
    Assertions.assertThat(response.jsonPath().getList("stations.id", Long.class))
        .containsExactly(봉천역, 서울대입구역, 낙성대역, 사당역, 총신대입구역, 남성역, 숭실대입구역);
  }

  /**
   * 예외1 - 출발역과 도착역이 같은 경우
   *
   * Given 하나 이상의 노선을 가진 임의의 지하철 노선도에서
   * When 경로조회시 검색조건의 출발역과 도착역이 같으면
   * Then 역 목록에는 하나의 역만을, 거리는 0을 리턴한다.
   */
  @Test
  @DisplayName("출발역과 도착역이 같은 경우 경로를 조회한다.")
  void 경로조회_예외_1() {
    // when
    ExtractableResponse<Response> response = 지하철_역_경로조회(봉천역, 봉천역);

    // then
    Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    Assertions.assertThat(response.jsonPath().getLong("distance")).isEqualTo(0L);
    Assertions.assertThat(response.jsonPath().getList("stations.id", Long.class))
        .containsExactly(봉천역);
  }

  /**
   * 예외2 - 출발역과 도착역이 연결이 되어 있지 않은 경우
   *
   * Given 하나 이상의 노선을 가진 임의의 지하철 노선도에서
   * When 경로조회시 검색조건의 출발역과 도착역이 연결되어있지 않으면
   * Then 경로를 조회 할 수 없다.
   */
  @Test
  @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 경로를 조회한다.")
  void 경로조회_예외_2() {
    // when
    ExtractableResponse<Response> response = 지하철_역_경로조회(봉천역, _999_시작역);

    // then
    Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    Assertions.assertThat(response.jsonPath().getString("serviceErrorCode")).isEqualTo("path-1000");
  }


  /**
   * 예외3 - 존재하지 않은 출발역이나 도착역으로 경로를 조회할 경우
   *
   * Given 하나 이상의 노선을 가진 임의의 지하철 노선도에서
   * When 경로조회시 검색조건의 출발역이나 도착역이 존재하지 않으면
   * Then 경로를 조회 할 수 없다.
   */
  @Test
  @DisplayName("존재하지 않은 출발역이나 도착역으로 경로를 조회한다.")
  void 경로조회_예외_3() {

    // given
    Long 존재하지_않는_역_번호 = 999999999L;

    // when
    ExtractableResponse<Response> response = 지하철_역_경로조회(봉천역, 존재하지_않는_역_번호);

    // then
    Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    Assertions.assertThat(response.jsonPath().getString("serviceErrorCode")).isEqualTo("station-1001");
  }

  /**
   * 예외4 - 조회조건에 출발역이나 도착역이 없는 상태로 조회하는 경우
   *
   * Given 하나 이상의 노선을 가진 임의의 지하철 노선도에서 출발역이나 도착역이 없는상태로
   * When 경로를 조회하면
   * Then 경로를 조회 할 수 없다.
   */
  @Test
  @DisplayName("조회조건에 출발역이나 도착역이 없는 상태로 경로를 조회한다.")
  void 경로조회_예외_4() {
    // when
    ExtractableResponse<Response> response = 지하철_역_경로조회_도착역_없음(봉천역);

    // then
    Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    Assertions.assertThat(response.jsonPath().getString("serviceErrorCode")).isEqualTo("validation-1000");
  }

  private static void _2호선_init() {
    봉천역 = 지하철역_생성_요청("봉천역").jsonPath().getLong("id");
    서울대입구역 = 지하철역_생성_요청("서울대입구역").jsonPath().getLong("id");
    낙성대역 = 지하철역_생성_요청("낙성대역").jsonPath().getLong("id");

    Map<String, String> lineCreateParams = createSectionCreateParams("2호선", "#cfcfcf", 3L, 봉천역, 서울대입구역);
    _2호선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    지하철_노선에_지하철_구간_생성_요청(_2호선, createAddSectionParams(서울대입구역, 낙성대역, 4L));
    지하철_노선에_지하철_구간_생성_요청(_2호선, createAddSectionParams(낙성대역, 사당역, 5L));
  }

  private static void _4호선_init() {
    동작역 = 지하철역_생성_요청("동작역").jsonPath().getLong("id");

    Map<String, String> lineCreateParams = createSectionCreateParams("4호선", "#ababab", 5L, 사당역, 총신대입구역);
    _4호선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    지하철_노선에_지하철_구간_생성_요청(_4호선, createAddSectionParams(총신대입구역, 동작역, 5L));
  }

  private static void _7호선_init() {
    남성역 = 지하철역_생성_요청("남성역").jsonPath().getLong("id");
    숭실대입구역 = 지하철역_생성_요청("숭실대입구역").jsonPath().getLong("id");

    Map<String, String> lineCreateParams = createSectionCreateParams("7호선", "#cdcdcd", 5L, 총신대입구역, 남성역);
    _7호선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    지하철_노선에_지하철_구간_생성_요청(_7호선, createAddSectionParams(남성역, 숭실대입구역, 5L));
  }

  private static void _999호선_init() {
    _999_시작역 = 지하철역_생성_요청("_999_시작역").jsonPath().getLong("id");
    _999_종점역 = 지하철역_생성_요청("_999_종점역").jsonPath().getLong("id");

    Map<String, String> lineCreateParams = createSectionCreateParams("999호선", "#ffffff", 5L, _999_시작역, _999_종점역);
    _999호선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
  }


  private static Map<String, String> createSectionCreateParams(String name, String color, Long distance, Long upStationId, Long downStationId) {
    Map<String, String> params = new HashMap<>();
    params.put("name", name);
    params.put("color", color);
    params.put("upStationId", upStationId + "");
    params.put("downStationId", downStationId + "");
    params.put("distance", distance + "");
    return params;
  }

  private static Map<String, String> createAddSectionParams(Long upStationId, Long downStationId, Long distance) {
    Map<String, String> lineCreateParams;
    lineCreateParams = new HashMap<>();
    lineCreateParams.put("upStationId", upStationId + "");
    lineCreateParams.put("downStationId", downStationId + "");
    lineCreateParams.put("distance", distance + "");
    return lineCreateParams;
  }
}
