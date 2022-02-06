package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class PathFinderSteps {
    public static ExtractableResponse<Response> 경로_탐색(Map<String, Object> params) {
        return RestAssured
                .given().log().all()
                .params(params)

                .when()
                .get("/paths")

                .then().log().all()
                .extract();
    }
}
