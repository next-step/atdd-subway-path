package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {
  public static ExtractableResponse<Response> 최단거리_검색(Long source, Long target) {
    return RestAssured
      .given().log().all()
      .when().get("/paths?source={source}&target={target}", source, target)
      .then().log().all().extract();
  }

  public static void 상태_조회_성공_확인(ExtractableResponse<Response> searchResponse) {
    assertThat(searchResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
  }

  public static void 상태_조회_잘못된_요청_확인(ExtractableResponse<Response> searchResponse) {
    assertThat(searchResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }
}
