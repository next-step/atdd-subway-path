package nextstep.subway.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

    private static final String BASE_URL = "/paths";

    public static ExtractableResponse<Response> 최단_경로_조회(Long source, Long target) {
        return RestAssured.given().log().all()
                .when().get(BASE_URL + "?source=" + source + "&target=" + target)
                .then().log().all()
                .extract();
    }
}
