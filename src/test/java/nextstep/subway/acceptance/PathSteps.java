package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class PathSteps {
    public static ExtractableResponse<Response> 경로_조회_요청(Long sourceId, Long targetId) {
        Map<String, String> params = new HashMap<>();
        params.put("source", sourceId.toString());
        params.put("target", targetId.toString());

        return RestAssured.given().log().all()
            .params(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/path")
            .then().log().all()
            .extract();
    }
}
