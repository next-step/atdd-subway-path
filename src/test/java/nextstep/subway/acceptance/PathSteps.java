package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class PathSteps {
    public static ExtractableResponse<Response> 경로_조회_요청(Long sourceStationId, Long targetStationId) {
        Map<String, Long> params = new HashMap<>();

        params.put("source", sourceStationId);
        params.put("target", targetStationId);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .params(params)
                .get("/paths")
                .then().log().all()
                .extract();
    }
}
