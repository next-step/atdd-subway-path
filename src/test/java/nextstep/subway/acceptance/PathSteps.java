package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {
    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .when().get("/paths?source={source}&target={target}", source, target)
                .then().log().all().extract();
    }
}
