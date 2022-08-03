package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

    public static ExtractableResponse<Response> 최단_경로_조회_요청(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .when().get(String.format("/paths?source=%d&target=%d", source, target))
                .then().log().all().extract();
    }

}
