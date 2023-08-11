package nextstep.subway.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

  public static ExtractableResponse<Response> 지하철_역_경로조회(Long 출발역, Long 도착역) {
    return RestAssured.given().log().all()
        .when().get("/paths?source={source}&target={target}", 출발역, 도착역)
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> 지하철_역_경로조회_도착역_없음(Long 출발역) {
    return RestAssured.given().log().all()
        .when().get("/paths?source={source}", 출발역)
        .then().log().all()
        .extract();
  }
}
