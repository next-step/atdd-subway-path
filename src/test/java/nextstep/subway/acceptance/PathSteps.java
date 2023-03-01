package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class PathSteps {
    public static ExtractableResponse<Response> 구간_조회_요청(Long source, Long target) {
        Map<String, String> params = new HashMap<>();
        params.put("source", source + "");
        params.put("target", target + "");
        return RestAssured
                .given().log().all()
                .params(params)
                .when().get("/paths")
                .then().log().all().extract();
    }
}
