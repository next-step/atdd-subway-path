package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LineSteps {

  public static Long 지하철_노선_생성_요청_후_ID_반환(String name, String color, Long upStationId, Long downStationId, int distance) {
    Map<String, String> params = createLineCreateParams(name, color, upStationId, downStationId, distance);
    return RestAssured
      .given().log().all()
      .body(params)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all().extract().jsonPath().getLong("id");
  }

  public static ExtractableResponse<Response> 지하철_노선만_생성_요청(String name, String color) {
    Map<String, String> params = createLineCreateParams(name, color);
    return RestAssured
      .given().log().all()
      .body(params)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all().extract();
  }

  public static Long 지하철_노선_생성_요청_후_ID_반환(Map<String, String> params) {
    return 지하철_노선만_생성_요청(params).jsonPath().getLong("id");
  }

  public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
    return RestAssured
      .given().log().all()
      .when().get("/lines")
      .then().log().all().extract();
  }

  public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
    return RestAssured
      .given().log().all()
      .when().get(createResponse.header("location"))
      .then().log().all().extract();
  }

  public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
    return RestAssured
      .given().log().all()
      .when().get("/lines/{id}", id)
      .then().log().all().extract();
  }

  public static void 지하철_노선_조회_요청_후_역_검증(Long lineId, List<Long> stationIds) {
    ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(stationIds.toArray(new Long[0]));
  }


  public static ExtractableResponse<Response> 지하철_노선만_생성_요청(Map<String, String> params) {
    return RestAssured
      .given().log().all()
      .body(params)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .when().post("/lines")
      .then().log().all().extract();
  }


  public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(Long lineId, Map<String, String> params) {
    return RestAssured.given().log().all()
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .body(params)
      .when().post("/lines/{lineId}/sections", lineId)
      .then().log().all().extract();
  }

  public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_요청(Long lineId, Long stationId) {
    return RestAssured.given().log().all()
      .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
      .then().log().all().extract();
  }

  private static Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
    Map<String, String> lineCreateParams;
    lineCreateParams = new HashMap<>();
    lineCreateParams.put("name", name);
    lineCreateParams.put("color", color);
    lineCreateParams.put("upStationId", upStationId + "");
    lineCreateParams.put("downStationId", downStationId + "");
    lineCreateParams.put("distance", distance + "");
    return lineCreateParams;
  }

  private static Map<String, String> createLineCreateParams(String name, String color) {
    Map<String, String> lineCreateParams;
    lineCreateParams = new HashMap<>();
    lineCreateParams.put("name", name);
    lineCreateParams.put("color", color);
    return lineCreateParams;
  }
}
