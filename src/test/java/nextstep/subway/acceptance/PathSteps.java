package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

    public static ExtractableResponse<Response> 경로_조회_요청(Long 출발역, Long 도착역) {
        String path = String.format("paths?source=%d&target=%d", 출발역, 도착역);
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .extract();
    }
}
