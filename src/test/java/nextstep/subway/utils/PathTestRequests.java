package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.path.controller.dto.PathResponse;

public class PathTestRequests {

  public static ExtractableResponse<Response> 지하철_경로_조회(Long source, Long target) {
    String path = "/paths?source=" + source + "&target=" + target;
    return RestAssured.given().log().all()
        .when().get(path)
        .then().log().all()
        .extract();
  }

  public static PathResponse 지하철_경로_조회_응답값_조회(ExtractableResponse<Response> result) {
    return result.jsonPath().getObject("", PathResponse.class);
  }
}
