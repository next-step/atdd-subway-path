package nextstep.subway.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

    public static ExtractableResponse<Response> 최단_경로_조회(String BASE_URL) {
        return RestAssured.given().log().all()
                .when().get(BASE_URL)
                .then().log().all()
                .extract();
    }
}
