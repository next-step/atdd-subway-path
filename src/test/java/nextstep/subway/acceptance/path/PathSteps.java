package nextstep.subway.acceptance.path;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {
    private PathSteps() {}

    public static ExtractableResponse<Response> 경로_조회_요청(long startStationId, long endStationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("source", startStationId);
        params.put("target", endStationId);

        return RestAssured
            .given().log().all()
            .params(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().log().all().extract();
    }
}
