package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class StationRequestBuilder {

  public static Map<String, String> createStationRequestParams(String name) {
    Map<String, String> params = new HashMap<>();
    params.put("name", name);
    return params;
  }

  public static void 지하철역_삭제됨(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  public static void 지하철역_생성됨(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.header("Location")).isNotBlank();
  }

  public static void 지하철역_생성실패됨(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
    return RestAssured.given().log().all()
        .body(createStationRequestParams(name))
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/stations")
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> 지하철역_목록조회_요청() {
    return RestAssured.given().log().all()
        .when()
        .get("/stations")
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> 지하철역_삭제_요청(String uri) {
    return RestAssured.given().log().all()
        .when()
        .delete(uri)
        .then().log().all()
        .extract();
  }
}
