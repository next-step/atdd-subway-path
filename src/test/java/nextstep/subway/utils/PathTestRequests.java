package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathTestRequests {

  public static ExtractableResponse<Response> 지하철_경로_조회(Long source, Long target) {
    String path = "/paths?source=" + source + "&target=" + target;
    return RestAssured.given().log().all()
        .when().get(path)
        .then().log().all()
        .extract();
  }
}
