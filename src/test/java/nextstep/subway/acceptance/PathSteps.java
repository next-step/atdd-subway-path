package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class PathSteps {
    public static ExtractableResponse<Response> 경로_조회(Long sourceStationId, Long targetStationId) {
        Map<String, Object> params = new HashMap();
        params.put("source", sourceStationId);
        params.put("target", targetStationId);
        return RestAssured.given().log().all().params(params)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
