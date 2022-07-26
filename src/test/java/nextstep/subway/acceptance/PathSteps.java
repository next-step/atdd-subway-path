package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long sourceId, Long targetId) {
        Map<String, Long> params = new HashMap<>();
        params.put("source", sourceId);
        params.put("target", targetId);

        return RestAssured
                .given().log().all()
                .queryParams(params)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
