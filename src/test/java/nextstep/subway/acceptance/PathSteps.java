package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_노선_최단거리_조회_요청(Long startId, Long endId) {
        return RestAssured
                .given().log().all()
                .when().get("/paths?source={startId}&target={endId}", startId, endId)
                .then().log().all().extract();
    }
}
