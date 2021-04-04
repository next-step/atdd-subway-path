package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

    public static ExtractableResponse<Response> 경로_조회_요청(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .params("source", source)
                .params("target", target)
                .when().get("/paths")
                .then().log().all().extract();
    }
}
