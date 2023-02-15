package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {
    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long sourceId, Long targetId) {
        return RestAssured
                .given().log().all()
                .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
                .then().log().all().extract();
    }
}
