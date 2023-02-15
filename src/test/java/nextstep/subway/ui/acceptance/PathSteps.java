package nextstep.subway.ui.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class PathSteps {
    public static ExtractableResponse<Response> 경로_탐색_요청(Long source, Long target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/paths?source=" + source + "&target=" + target)
                .then().log().all()
                .extract();
    }
}
